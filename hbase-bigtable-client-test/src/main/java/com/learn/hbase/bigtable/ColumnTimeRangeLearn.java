package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import java.io.IOException;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import static com.learn.hbase.bigtable.util.MyUtil.qualifier;
import static com.learn.hbase.bigtable.util.MyUtil.timeInLong;
import static com.learn.hbase.bigtable.util.MyUtil.value;

/**
 * This is meant to understand <a
 * href="https://stackoverflow.com/questions/56979965/bigtable-column-family-time-range-scan-returning-all-rows-regardless-of-timestam">this</a>
 * question.
 *
 * <p>I am trying to use a ColumnFamilyTimeRange on my Scan to read only recent rows from Bigtable.
 * However, the scan returns all rows no matter what I set the time range to.
 *
 * <p>I have one column family. Here's what I'm seeing: I add a new row with a value for that column
 * family, then wait, then add another new row. I then do a read from Bigtable with a Scan with an
 * ordinary (ie not column family specific) TimeRange set. It correctly returns only the recently
 * added row.
 *
 * <p>However, when I change that TimeRange to a ColumnFamilyTimeRange with the same timestamp
 * bounds and the only column family I have, I get back every row. Even when I set the timestamp
 * bounds to something nonsensical (such as before I even created the table), I still get back every
 * row.
 *
 * <p>Is this a bug or am I completely missing how ColumnFamilyTimeRange is meant to work?
 */
public class ColumnTimeRangeLearn {

  private static final int TOTAL_COL = 5;
  private static final String TABLE_ID = "MyTestTable";
  private final HBaseBoot booter;

  private final String rowKey1 = "rowkey_1";
  private final String rowKey2 = "rowkey_2";
  private final String rowKey3 = "rowkey_3";

  private Table table;

  ColumnTimeRangeLearn() throws Exception {
    booter = new HBaseBoot();
  }

  private void addFiveRows(String rowKey) throws IOException {
    Put put = new Put(rowKey.getBytes());

    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf1".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf2".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf3".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    table.put(put);
  }

  private void scanTable() throws IOException {
    Scan scan = new Scan();
    //    scan.withStartRow(rowKey1.getBytes()).withStopRow(rowKey3.getBytes());
    printResultScanner(table.getScanner(scan));
  }

  public static void main(String[] args) throws Exception {
    ColumnTimeRangeLearn ct = new ColumnTimeRangeLearn();
    ct.table = ct.booter.connection.getTable(TableName.valueOf(TABLE_ID));
    // Admin operations
    //    ct.booter.admin.deleteTable(TableName.valueOf(TABLE_ID));
    //    ct.createAndAdd();

    // simple table scan
//        ct.scanTable();
    //    System.out.println("--------------------------------");

    ct.scanTimeStampWithCF();
    System.out.println("--------------------------------");
    //        ct.scanTimeStamp();
  }

  private void createAndAdd() throws IOException {
    booter.createTable(TABLE_ID, "cf1", "cf2", "cf3");

    // Adding some data for test
    addFiveRows(rowKey1);
    addFiveRows(rowKey2);
    addFiveRows(rowKey3);

    addFiveRows(rowKey1);
    addFiveRows(rowKey2);
    addFiveRows(rowKey3);
  }

  private void printResultScanner(ResultScanner rs) throws IOException {
    Result result = rs.next();
    while (result != null) {
      System.out.println("Row: " + Bytes.toString(result.getRow()));
      for (Cell cell : result.rawCells()) {
        System.out.println(
            "Family: "
                + Bytes.toString(cell.getFamilyArray())
                + "\nQualifier: "
                + Bytes.toString(cell.getQualifierArray())
                + "\nTimestamp: "
                + cell.getTimestamp()
                + "\nValue:"
                + Bytes.toString(cell.getValueArray()));
        System.out.println();
      }
      result = rs.next();
    }
  }

  private void printTimestampOnly(ResultScanner rs) throws IOException {
    Result result = rs.next();
    int counter = 0;
    while (result != null) {
      System.out.println("Row: " + Bytes.toString(result.getRow()));
      for (Cell cell : result.rawCells()) {
        System.out.println(++counter);
        System.out.println(
            "\tFamily: "
                + Bytes.toString(cell.getFamilyArray())
                + "\n \tTimestamp: "
                + cell.getTimestamp());
        System.out.println();
      }
      result = rs.next();
    }
  }

  private void scanTimeStamp() throws IOException {
    Scan scan = new Scan().setTimeRange(1503000000000L, 1503999999999L);
    printResultScanner(table.getScanner(scan));
  }

  private void scanTimeStampWithCF() throws IOException {
    Scan scan =

        // Bigtable
                new Scan()
                    .setColumnFamilyTimeRange("cf1".getBytes(), 942517717954L, 942517752782L)
                    .setColumnFamilyTimeRange("cf2".getBytes(), 1503167215265L, 1503167283429L)
                    .setColumnFamilyTimeRange("cf3".getBytes(), 1554316104295L, 1554316199999L)
                    .setColumnFamilyTimeRange("cf3".getBytes(), 1503167212213L, 1503167375633L);


        // HBase
//        new Scan()
//            .setColumnFamilyTimeRange("cf1".getBytes(), 1503167301508L, 1503167390224L)
//            .setColumnFamilyTimeRange("cf2".getBytes(), 942517705182L, 942517799354L)
//            .setColumnFamilyTimeRange("cf3".getBytes(), 1554316100523L, 1554316152312L)
//            .setColumnFamilyTimeRange("cf3".getBytes(), 1503167296765L, 1503167362401L);

    printTimestampOnly(table.getScanner(scan));
  }
}

/*jh

Row: rowkey_1
	Family: cf1	Qualifier: qualifier2NLRN	 Timestamp: 942517798486	Value:lJXIHJAkop
	Family: cf1	Qualifier: qualifier9NAfw	 Timestamp: 942517730982	Value:OYxxVPufNE
	Family: cf1	Qualifier: qualifierDJpnS	 Timestamp: 1503167226297	Value:sJvTVuPAax
	Family: cf1	Qualifier: qualifierFCvSX	 Timestamp: 942517719890	Value:CqiHiprXlg
	Family: cf1	Qualifier: qualifierKHAkz	 Timestamp: 1503167204329	Value:VJoIVifPgr
	Family: cf1	Qualifier: qualifierKqPLb	 Timestamp: 942517726666	Value:UdkLJPISns
	Family: cf1	Qualifier: qualifierO06IV	 Timestamp: 1503167325932	Value:JQcCfdWOky
	Family: cf1	Qualifier: qualifierXAXDq	 Timestamp: 1503167202277	Value:zyvjKeWJXF
	Family: cf1	Qualifier: qualifierdaXkT	 Timestamp: 1503167339092	Value:XhNFwiIFHR
	Family: cf1	Qualifier: qualifieroKu47	 Timestamp: 942517765398	Value:mDmpywKphO
	Family: cf2	Qualifier: qualifier2ZcEa	 Timestamp: 942517708286	Value:khNGUjyJMT
	Family: cf2	Qualifier: qualifierCeQnf	 Timestamp: 1503167330732	Value:KhaKjoXaFp
	Family: cf2	Qualifier: qualifierNCR9j	 Timestamp: 942517778958	Value:eNMMnidEwc
	Family: cf2	Qualifier: qualifierUj6iC	 Timestamp: 942517732374	Value:RNkNksswxm
	Family: cf2	Qualifier: qualifierVT6Ey	 Timestamp: 1503167283429	Value:vNAlbXHAGY
	Family: cf2	Qualifier: qualifieraLlUE	 Timestamp: 1503167314448	Value:CjxQngrweD
	Family: cf2	Qualifier: qualifiere7MxE	 Timestamp: 1503167282933	Value:LGMsGllpyK
	Family: cf2	Qualifier: qualifiernZzuf	 Timestamp: 1503167208437	Value:ZlugeevDJz
	Family: cf2	Qualifier: qualifierqaOYo	 Timestamp: 1554316194935	Value:fhXhprVfAA
	Family: cf2	Qualifier: qualifierqyIhE	 Timestamp: 942517720246	Value:fusyqoiUOS
	Family: cf3	Qualifier: qualifierDd7sl	 Timestamp: 942517745194	Value:yhkLxUCCJJ
	Family: cf3	Qualifier: qualifierGcHIQ	 Timestamp: 1503167307480	Value:FktSzWUFCk
	Family: cf3	Qualifier: qualifierIMKYV	 Timestamp: 942517718358	Value:fraUaMYpIY
	Family: cf3	Qualifier: qualifierKwlP4	 Timestamp: 942517775806	Value:kSceQsUUby
	Family: cf3	Qualifier: qualifierOmMom	 Timestamp: 942517750598	Value:hmVxmrhiIv
	Family: cf3	Qualifier: qualifierTUtYy	 Timestamp: 1503167307856	Value:EwiPoTnKQr
	Family: cf3	Qualifier: qualifiergX39o	 Timestamp: 1503167308656	Value:FsBhkoalXb
	Family: cf3	Qualifier: qualifierqFFni	 Timestamp: 1503167346632	Value:wBTlZeCUVt
	Family: cf3	Qualifier: qualifieryUAvz	 Timestamp: 942517717954	Value:fPMbypGcms
	Family: cf3	Qualifier: qualifieryf7P5	 Timestamp: 1554316115667	Value:STjIUJgJap
Row: rowkey_2
	Family: cf1	Qualifier: qualifier1MgTw	 Timestamp: 1554316102799	Value:vHrUvrEFkc
	Family: cf1	Qualifier: qualifier4MfMb	 Timestamp: 1503167388648	Value:lZuagPYVtS
	Family: cf1	Qualifier: qualifierC2BPs	 Timestamp: 942517745502	Value:DioRzsskEU
	Family: cf1	Qualifier: qualifierJLYiy	 Timestamp: 1503167387308	Value:SsAUokNNDT
	Family: cf1	Qualifier: qualifierKYitj	 Timestamp: 942517778930	Value:epZOrEvyDy
	Family: cf1	Qualifier: qualifierc4sga	 Timestamp: 942517743218	Value:itGBQcYDhL
	Family: cf1	Qualifier: qualifiern9ujU	 Timestamp: 1554316158431	Value:VxgMPiMLTT
	Family: cf1	Qualifier: qualifieroUzOJ	 Timestamp: 1503167360760	Value:ciKZAXgPip
	Family: cf1	Qualifier: qualifiertUyjH	 Timestamp: 942517789026	Value:FRMsmZrztx
	Family: cf1	Qualifier: qualifieryc8aa	 Timestamp: 1503167368132	Value:dABetfHlOh
	Family: cf2	Qualifier: qualifier7z0xD	 Timestamp: 1554316138639	Value:QHKwGqIXvw
	Family: cf2	Qualifier: qualifierBLHe2	 Timestamp: 1503167302756	Value:lfWuVkWPTo
	Family: cf2	Qualifier: qualifierDWPpo	 Timestamp: 1503167245681	Value:KzTYjzIdfF
	Family: cf2	Qualifier: qualifierYUb9p	 Timestamp: 1554316189263	Value:fuugNbVfEd
	Family: cf2	Qualifier: qualifiermClA8	 Timestamp: 1503167282401	Value:VqUHafHYup
	Family: cf2	Qualifier: qualifieroS5VR	 Timestamp: 1503167336724	Value:cUraaPthVt
	Family: cf2	Qualifier: qualifiertVmhG	 Timestamp: 1554316176959	Value:IFMqFenVhZ
	Family: cf2	Qualifier: qualifieruLKmV	 Timestamp: 1503167312976	Value:CPNDGLtxvI
	Family: cf2	Qualifier: qualifierx8zlU	 Timestamp: 1554316156803	Value:mhOpdgsBWU
	Family: cf2	Qualifier: qualifieryswfW	 Timestamp: 942517709174	Value:XYaqgOolcM
	Family: cf3	Qualifier: qualifier8iWIN	 Timestamp: 1554316190979	Value:gWwUrJPgLK
	Family: cf3	Qualifier: qualifierFfWNV	 Timestamp: 1554316107971	Value:KaJPimlIam
	Family: cf3	Qualifier: qualifierJOwOI	 Timestamp: 1503167254765	Value:KrpWiJLdPp
	Family: cf3	Qualifier: qualifierLY6ms	 Timestamp: 942517759306	Value:WnYVoFgTyR
	Family: cf3	Qualifier: qualifierSdOEr	 Timestamp: 1503167248905	Value:wCnkLDrtyu
	Family: cf3	Qualifier: qualifierSvrJc	 Timestamp: 1503167375632	Value:IgxzgQpEHb
	Family: cf3	Qualifier: qualifierWC26J	 Timestamp: 1554316193675	Value:tZXcuhdsMP
	Family: cf3	Qualifier: qualifierWMR6J	 Timestamp: 1503167235429	Value:IeWUzIPQnq
	Family: cf3	Qualifier: qualifierWUaKw	 Timestamp: 1554316150675	Value:ETyZocBgpu
	Family: cf3	Qualifier: qualifierzhpWv	 Timestamp: 1554316168819	Value:SOUmMEWUvj
Row: rowkey_3
	Family: cf1	Qualifier: qualifier4H5Jf	 Timestamp: 1503167392572	Value:HfAzWJBGJA
	Family: cf1	Qualifier: qualifier6WiWW	 Timestamp: 1503167245153	Value:oSSkwbUgaK
	Family: cf1	Qualifier: qualifierF8shj	 Timestamp: 942517705102	Value:nJuzyLEYjF
	Family: cf1	Qualifier: qualifierL8BnQ	 Timestamp: 1503167219441	Value:eomFUWOtWD
	Family: cf1	Qualifier: qualifierSU3xs	 Timestamp: 942517752782	Value:pSrpKhDJJA
	Family: cf1	Qualifier: qualifierUTp2r	 Timestamp: 1554316169091	Value:AcSbHifJua
	Family: cf1	Qualifier: qualifierWuEC1	 Timestamp: 1503167262177	Value:asHsHYSGHc
	Family: cf1	Qualifier: qualifierc3N9A	 Timestamp: 1503167359812	Value:keqyvxIJXj
	Family: cf1	Qualifier: qualifierpEg8c	 Timestamp: 1554316162827	Value:MGHGDtbZuw
	Family: cf1	Qualifier: qualifierydIWt	 Timestamp: 1503167264573	Value:gNIfqJWiuM
	Family: cf2	Qualifier: qualifier2JSXs	 Timestamp: 1503167338696	Value:qlmNEBCOAp
	Family: cf2	Qualifier: qualifier5B3F9	 Timestamp: 1503167215265	Value:QoiAbUkQLp
	Family: cf2	Qualifier: qualifierHy8k7	 Timestamp: 1554316198907	Value:RPLbCYmTmV
	Family: cf2	Qualifier: qualifierJ9gcm	 Timestamp: 942517792790	Value:BSkBgudsQP
	Family: cf2	Qualifier: qualifierLnUKs	 Timestamp: 942517771502	Value:gIZHAsqjhr
	Family: cf2	Qualifier: qualifierhbzog	 Timestamp: 942517736706	Value:FbUNRJzKTB
	Family: cf2	Qualifier: qualifierk9tIO	 Timestamp: 1503167268889	Value:GpIiEkgOWs
	Family: cf2	Qualifier: qualifiersHysI	 Timestamp: 942517759478	Value:kbaaAJxYCc
	Family: cf2	Qualifier: qualifierxA4mb	 Timestamp: 942517725170	Value:LalpqfosyR
	Family: cf2	Qualifier: qualifierz8fxg	 Timestamp: 942517787518	Value:PurxqrRWLK
	Family: cf3	Qualifier: qualifier8N65m	 Timestamp: 1503167398824	Value:rPMjyQCOqk
	Family: cf3	Qualifier: qualifierGMYOv	 Timestamp: 942517751590	Value:aeIciqLyhb
	Family: cf3	Qualifier: qualifierHPJ60	 Timestamp: 1503167291021	Value:SzyHBHpcSJ
	Family: cf3	Qualifier: qualifierNNkQF	 Timestamp: 942517790398	Value:ieCxixpTjj
	Family: cf3	Qualifier: qualifierS0QMk	 Timestamp: 1503167371000	Value:pjAYhzlCjq
	Family: cf3	Qualifier: qualifiereUhwC	 Timestamp: 1554316188815	Value:sksCLEzJAq
	Family: cf3	Qualifier: qualifierfONGD	 Timestamp: 1554316104295	Value:QgsZQAOkVr
	Family: cf3	Qualifier: qualifierj3YZ2	 Timestamp: 1554316199039	Value:rDwFfXxZJs
	Family: cf3	Qualifier: qualifierl0Rft	 Timestamp: 1503167212213	Value:XTNxNsQsTf
	Family: cf3	Qualifier: qualifierlit8o	 Timestamp: 1503167269309	Value:DEdLmKwkYM

 */
