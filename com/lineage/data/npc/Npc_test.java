package com.lineage.data.npc;

import com.lineage.DatabaseFactory;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 兌換商人-勇者徽章<BR>
 * 85029<BR>
 *
 * @author dexc
 */
public class Npc_test extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_test.class);

    private Npc_test() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_test();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            String tableName = "";
            String[] msg = null;
            if (npc.getNpcId() == 200220) {
                tableName = "armor";
                msg = test(tableName, 0, 1500);
            } else if (npc.getNpcId() == 200221) {
                tableName = "weapon";
                msg = test(tableName, 0, 1500);
            } else if (npc.getNpcId() == 200222) {
                tableName = "etcitem";
                msg = test(tableName, 0, 1500);
            }
            final String[] info = {msg[0], msg[1], msg[2], msg[3], msg[4], msg[5], msg[6], msg[7], msg[8], msg[9], msg[10], msg[11], msg[12], msg[13], msg[14], msg[15], msg[16], msg[17], msg[18], msg[19], msg[20], msg[21], msg[22], msg[23], msg[24], msg[25], msg[26], msg[27], msg[28], msg[29], msg[30], msg[31], msg[32], msg[33], msg[34], msg[35], msg[36], msg[37], msg[38], msg[39], msg[40], msg[41], msg[42], msg[43], msg[44], msg[45], msg[46], msg[47], msg[48], msg[49], msg[50], msg[51], msg[52], msg[53], msg[54], msg[55], msg[56], msg[57], msg[58], msg[59], msg[60], msg[61], msg[62], msg[63], msg[64], msg[65], msg[66], msg[67], msg[68], msg[69], msg[70], msg[71], msg[72], msg[73], msg[74], msg[75], msg[76], msg[77], msg[78], msg[79], msg[80], msg[81], msg[82], msg[83], msg[84], msg[85], msg[86], msg[87], msg[88], msg[89], msg[90], msg[91], msg[92], msg[93], msg[94], msg[95], msg[96], msg[97], msg[98], msg[99], msg[100], msg[101], msg[102], msg[103], msg[104], msg[105], msg[106], msg[107], msg[108], msg[109], msg[110], msg[111], msg[112], msg[113], msg[114], msg[115], msg[116], msg[117], msg[118], msg[119], msg[120], msg[121], msg[122], msg[123], msg[124], msg[125], msg[126], msg[127], msg[128], msg[129], msg[130], msg[131], msg[132], msg[133], msg[134], msg[135], msg[136], msg[137], msg[138], msg[139], msg[140], msg[141], msg[142], msg[143], msg[144], msg[145], msg[146], msg[147], msg[148], msg[149], msg[150], msg[151], msg[152], msg[153], msg[154], msg[155], msg[156], msg[157], msg[158], msg[159], msg[160], msg[161], msg[162], msg[163], msg[164], msg[165], msg[166], msg[167], msg[168], msg[169], msg[170], msg[171], msg[172], msg[173], msg[174], msg[175], msg[176], msg[177], msg[178], msg[179], msg[180], msg[181], msg[182], msg[183], msg[184], msg[185], msg[186], msg[187], msg[188], msg[189], msg[190], msg[191], msg[192], msg[193], msg[194], msg[195], msg[196], msg[197], msg[198], msg[199], msg[200], msg[201], msg[202], msg[203], msg[204], msg[205], msg[206], msg[207], msg[208], msg[209], msg[210], msg[211], msg[212], msg[213], msg[214], msg[215], msg[216], msg[217], msg[218], msg[219], msg[220], msg[221], msg[222], msg[223], msg[224], msg[225], msg[226], msg[227], msg[228], msg[229], msg[230], msg[231], msg[232], msg[233], msg[234], msg[235], msg[236], msg[237], msg[238], msg[239], msg[240], msg[241], msg[242], msg[243], msg[244], msg[245], msg[246], msg[247], msg[248], msg[249], msg[250], msg[251], msg[252], msg[253], msg[254], msg[255], msg[256], msg[257], msg[258], msg[259], msg[260], msg[261], msg[262], msg[263], msg[264], msg[265], msg[266], msg[267], msg[268], msg[269], msg[270], msg[271], msg[272], msg[273], msg[274], msg[275], msg[276], msg[277], msg[278], msg[279], msg[280], msg[281], msg[282], msg[283], msg[284], msg[285], msg[286], msg[287], msg[288], msg[289], msg[290], msg[291], msg[292], msg[293], msg[294], msg[295], msg[296], msg[297], msg[298], msg[299], msg[300], msg[301], msg[302], msg[303], msg[304], msg[305], msg[306], msg[307], msg[308], msg[309], msg[310], msg[311], msg[312], msg[313], msg[314], msg[315], msg[316], msg[317], msg[318], msg[319], msg[320], msg[321], msg[322], msg[323], msg[324], msg[325], msg[326], msg[327], msg[328], msg[329], msg[330], msg[331], msg[332], msg[333], msg[334], msg[335], msg[336], msg[337], msg[338], msg[339], msg[340], msg[341], msg[342], msg[343], msg[344], msg[345], msg[346], msg[347], msg[348], msg[349], msg[350], msg[351], msg[352], msg[353], msg[354], msg[355], msg[356], msg[357], msg[358], msg[359], msg[360], msg[361], msg[362], msg[363], msg[364], msg[365], msg[366], msg[367], msg[368], msg[369], msg[370], msg[371], msg[372], msg[373], msg[374], msg[375], msg[376], msg[377], msg[378], msg[379], msg[380], msg[381], msg[382], msg[383], msg[384], msg[385], msg[386], msg[387], msg[388], msg[389], msg[390], msg[391], msg[392], msg[393], msg[394], msg[395], msg[396], msg[397], msg[398], msg[399], msg[400], msg[401], msg[402], msg[403], msg[404], msg[405], msg[406], msg[407], msg[408], msg[409], msg[410], msg[411], msg[412], msg[413], msg[414], msg[415], msg[416], msg[417], msg[418], msg[419], msg[420], msg[421], msg[422], msg[423], msg[424], msg[425], msg[426], msg[427], msg[428], msg[429], msg[430], msg[431], msg[432], msg[433], msg[434], msg[435], msg[436], msg[437], msg[438], msg[439], msg[440], msg[441], msg[442], msg[443], msg[444], msg[445], msg[446], msg[447], msg[448], msg[449], msg[450], msg[451], msg[452], msg[453], msg[454], msg[455], msg[456], msg[457], msg[458], msg[459], msg[460], msg[461], msg[462], msg[463], msg[464], msg[465], msg[466], msg[467], msg[468], msg[469], msg[470], msg[471], msg[472], msg[473], msg[474], msg[475], msg[476], msg[477], msg[478], msg[479], msg[480], msg[481], msg[482], msg[483], msg[484], msg[485], msg[486], msg[487], msg[488], msg[489], msg[490], msg[491], msg[492], msg[493], msg[494], msg[495], msg[496], msg[497], msg[498], msg[499], msg[500], msg[501], msg[502], msg[503], msg[504], msg[505], msg[506], msg[507], msg[508], msg[509], msg[510], msg[511], msg[512], msg[513], msg[514], msg[515], msg[516], msg[517], msg[518], msg[519], msg[520], msg[521], msg[522], msg[523], msg[524], msg[525], msg[526], msg[527], msg[528], msg[529], msg[530], msg[531], msg[532], msg[533], msg[534], msg[535], msg[536], msg[537], msg[538], msg[539], msg[540], msg[541], msg[542], msg[543], msg[544], msg[545], msg[546], msg[547], msg[548], msg[549], msg[550], msg[551], msg[552], msg[553], msg[554], msg[555], msg[556], msg[557], msg[558], msg[559], msg[560], msg[561], msg[562], msg[563], msg[564], msg[565], msg[566], msg[567], msg[568], msg[569], msg[570], msg[571], msg[572], msg[573], msg[574], msg[575], msg[576], msg[577], msg[578], msg[579], msg[580], msg[581], msg[582], msg[583], msg[584], msg[585], msg[586], msg[587], msg[588], msg[589], msg[590], msg[591], msg[592], msg[593], msg[594], msg[595], msg[596], msg[597], msg[598], msg[599], msg[600], msg[601], msg[602], msg[603], msg[604], msg[605], msg[606], msg[607], msg[608], msg[609], msg[610], msg[611], msg[612], msg[613], msg[614], msg[615], msg[616], msg[617], msg[618], msg[619], msg[620], msg[621], msg[622], msg[623], msg[624], msg[625], msg[626], msg[627], msg[628], msg[629], msg[630], msg[631], msg[632], msg[633], msg[634], msg[635], msg[636], msg[637], msg[638], msg[639], msg[640], msg[641], msg[642], msg[643], msg[644], msg[645], msg[646], msg[647], msg[648], msg[649], msg[650], msg[651], msg[652], msg[653], msg[654], msg[655], msg[656], msg[657], msg[658], msg[659], msg[660], msg[661], msg[662], msg[663], msg[664], msg[665], msg[666], msg[667], msg[668], msg[669], msg[670], msg[671], msg[672], msg[673], msg[674], msg[675], msg[676], msg[677], msg[678], msg[679], msg[680], msg[681], msg[682], msg[683], msg[684], msg[685], msg[686], msg[687], msg[688], msg[689], msg[690], msg[691], msg[692], msg[693], msg[694], msg[695], msg[696], msg[697], msg[698], msg[699], msg[700], msg[701], msg[702], msg[703], msg[704], msg[705], msg[706], msg[707], msg[708], msg[709], msg[710], msg[711], msg[712], msg[713], msg[714], msg[715], msg[716], msg[717], msg[718], msg[719], msg[720], msg[721], msg[722], msg[723], msg[724], msg[725], msg[726], msg[727], msg[728], msg[729], msg[730], msg[731], msg[732], msg[733], msg[734], msg[735], msg[736], msg[737], msg[738], msg[739], msg[740], msg[741], msg[742], msg[743], msg[744], msg[745], msg[746], msg[747], msg[748], msg[749], msg[750], msg[751], msg[752], msg[753], msg[754], msg[755], msg[756], msg[757], msg[758], msg[759], msg[760], msg[761], msg[762], msg[763], msg[764], msg[765], msg[766], msg[767], msg[768], msg[769], msg[770], msg[771], msg[772], msg[773], msg[774], msg[775], msg[776], msg[777], msg[778], msg[779], msg[780], msg[781], msg[782], msg[783], msg[784], msg[785], msg[786], msg[787], msg[788], msg[789], msg[790], msg[791], msg[792], msg[793], msg[794], msg[795], msg[796], msg[797], msg[798], msg[799], msg[800], msg[801], msg[802], msg[803], msg[804], msg[805], msg[806], msg[807], msg[808], msg[809], msg[810], msg[811], msg[812], msg[813], msg[814], msg[815], msg[816], msg[817], msg[818], msg[819], msg[820], msg[821], msg[822], msg[823], msg[824], msg[825], msg[826], msg[827], msg[828], msg[829], msg[830], msg[831], msg[832], msg[833], msg[834], msg[835], msg[836], msg[837], msg[838], msg[839], msg[840], msg[841], msg[842], msg[843], msg[844], msg[845], msg[846], msg[847], msg[848], msg[849], msg[850], msg[851], msg[852], msg[853], msg[854], msg[855], msg[856], msg[857], msg[858], msg[859], msg[860], msg[861], msg[862], msg[863], msg[864], msg[865], msg[866], msg[867], msg[868], msg[869], msg[870], msg[871], msg[872], msg[873], msg[874], msg[875], msg[876], msg[877], msg[878], msg[879], msg[880], msg[881], msg[882], msg[883], msg[884], msg[885], msg[886], msg[887], msg[888], msg[889], msg[890], msg[891], msg[892], msg[893], msg[894], msg[895], msg[896], msg[897], msg[898], msg[899], msg[900], msg[901], msg[902], msg[903], msg[904], msg[905], msg[906], msg[907], msg[908], msg[909], msg[910], msg[911], msg[912], msg[913], msg[914], msg[915], msg[916], msg[917], msg[918], msg[919], msg[920], msg[921], msg[922], msg[923], msg[924], msg[925], msg[926], msg[927], msg[928], msg[929], msg[930], msg[931], msg[932], msg[933], msg[934], msg[935], msg[936], msg[937], msg[938], msg[939], msg[940], msg[941], msg[942], msg[943], msg[944], msg[945], msg[946], msg[947], msg[948], msg[949], msg[950], msg[951], msg[952], msg[953], msg[954], msg[955], msg[956], msg[957], msg[958], msg[959], msg[960], msg[961], msg[962], msg[963], msg[964], msg[965], msg[966], msg[967], msg[968], msg[969], msg[970], msg[971], msg[972], msg[973], msg[974], msg[975], msg[976], msg[977], msg[978], msg[979], msg[980], msg[981], msg[982], msg[983], msg[984], msg[985], msg[986], msg[987], msg[988], msg[989], msg[990], msg[991], msg[992], msg[993], msg[994], msg[995], msg[996], msg[997], msg[998], msg[999], msg[1000], msg[1001], msg[1002], msg[1003], msg[1004], msg[1005], msg[1006], msg[1007], msg[1008], msg[1009], msg[1010], msg[1011], msg[1012], msg[1013], msg[1014], msg[1015], msg[1016], msg[1017], msg[1018], msg[1019], msg[1020], msg[1021], msg[1022], msg[1023], msg[1024], msg[1025], msg[1026], msg[1027], msg[1028], msg[1029], msg[1030], msg[1031], msg[1032], msg[1033], msg[1034], msg[1035], msg[1036], msg[1037], msg[1038], msg[1039], msg[1040], msg[1041], msg[1042], msg[1043], msg[1044], msg[1045], msg[1046], msg[1047], msg[1048], msg[1049], msg[1050], msg[1051], msg[1052], msg[1053], msg[1054], msg[1055], msg[1056], msg[1057], msg[1058], msg[1059], msg[1060], msg[1061], msg[1062], msg[1063], msg[1064], msg[1065], msg[1066], msg[1067], msg[1068], msg[1069], msg[1070], msg[1071], msg[1072], msg[1073], msg[1074], msg[1075], msg[1076], msg[1077], msg[1078], msg[1079], msg[1080], msg[1081], msg[1082], msg[1083], msg[1084], msg[1085], msg[1086], msg[1087], msg[1088], msg[1089], msg[1090], msg[1091], msg[1092], msg[1093], msg[1094], msg[1095], msg[1096], msg[1097], msg[1098], msg[1099], msg[1100], msg[1101], msg[1102], msg[1103], msg[1104], msg[1105], msg[1106], msg[1107], msg[1108], msg[1109], msg[1110], msg[1111], msg[1112], msg[1113], msg[1114], msg[1115], msg[1116], msg[1117], msg[1118], msg[1119], msg[1120], msg[1121], msg[1122], msg[1123], msg[1124], msg[1125], msg[1126], msg[1127], msg[1128], msg[1129], msg[1130], msg[1131], msg[1132], msg[1133], msg[1134], msg[1135], msg[1136], msg[1137], msg[1138], msg[1139], msg[1140], msg[1141], msg[1142], msg[1143], msg[1144], msg[1145], msg[1146], msg[1147], msg[1148], msg[1149], msg[1150], msg[1151], msg[1152], msg[1153], msg[1154], msg[1155], msg[1156], msg[1157], msg[1158], msg[1159], msg[1160], msg[1161], msg[1162], msg[1163], msg[1164], msg[1165], msg[1166], msg[1167], msg[1168], msg[1169], msg[1170], msg[1171], msg[1172], msg[1173], msg[1174], msg[1175], msg[1176], msg[1177], msg[1178], msg[1179], msg[1180], msg[1181], msg[1182], msg[1183], msg[1184], msg[1185], msg[1186], msg[1187], msg[1188], msg[1189], msg[1190], msg[1191], msg[1192], msg[1193], msg[1194], msg[1195], msg[1196], msg[1197], msg[1198], msg[1199], msg[1200], msg[1201], msg[1202], msg[1203], msg[1204], msg[1205], msg[1206], msg[1207], msg[1208], msg[1209], msg[1210], msg[1211], msg[1212], msg[1213], msg[1214], msg[1215], msg[1216], msg[1217], msg[1218], msg[1219], msg[1220], msg[1221], msg[1222], msg[1223], msg[1224], msg[1225], msg[1226], msg[1227], msg[1228], msg[1229], msg[1230], msg[1231], msg[1232], msg[1233], msg[1234], msg[1235], msg[1236], msg[1237], msg[1238], msg[1239], msg[1240], msg[1241], msg[1242], msg[1243], msg[1244], msg[1245], msg[1246], msg[1247], msg[1248], msg[1249], msg[1250], msg[1251], msg[1252], msg[1253], msg[1254], msg[1255], msg[1256], msg[1257], msg[1258], msg[1259], msg[1260], msg[1261], msg[1262], msg[1263], msg[1264], msg[1265], msg[1266], msg[1267], msg[1268], msg[1269], msg[1270], msg[1271], msg[1272], msg[1273], msg[1274], msg[1275], msg[1276], msg[1277], msg[1278], msg[1279], msg[1280], msg[1281], msg[1282], msg[1283], msg[1284], msg[1285], msg[1286], msg[1287], msg[1288], msg[1289], msg[1290], msg[1291], msg[1292], msg[1293], msg[1294], msg[1295], msg[1296], msg[1297], msg[1298], msg[1299], msg[1300], msg[1301], msg[1302], msg[1303], msg[1304], msg[1305], msg[1306], msg[1307], msg[1308], msg[1309], msg[1310], msg[1311], msg[1312], msg[1313], msg[1314], msg[1315], msg[1316], msg[1317], msg[1318], msg[1319], msg[1320], msg[1321], msg[1322], msg[1323], msg[1324], msg[1325], msg[1326], msg[1327], msg[1328], msg[1329], msg[1330], msg[1331], msg[1332], msg[1333], msg[1334], msg[1335], msg[1336], msg[1337], msg[1338], msg[1339], msg[1340], msg[1341], msg[1342], msg[1343], msg[1344], msg[1345], msg[1346], msg[1347], msg[1348], msg[1349], msg[1350], msg[1351], msg[1352], msg[1353], msg[1354], msg[1355], msg[1356], msg[1357], msg[1358], msg[1359], msg[1360], msg[1361], msg[1362], msg[1363], msg[1364], msg[1365], msg[1366], msg[1367], msg[1368], msg[1369], msg[1370], msg[1371], msg[1372], msg[1373], msg[1374], msg[1375], msg[1376], msg[1377], msg[1378], msg[1379], msg[1380], msg[1381], msg[1382], msg[1383], msg[1384], msg[1385], msg[1386], msg[1387], msg[1388], msg[1389], msg[1390], msg[1391], msg[1392], msg[1393], msg[1394], msg[1395], msg[1396], msg[1397], msg[1398], msg[1399], msg[1400], msg[1401], msg[1402], msg[1403], msg[1404], msg[1405], msg[1406], msg[1407], msg[1408], msg[1409], msg[1410], msg[1411], msg[1412], msg[1413], msg[1414], msg[1415], msg[1416], msg[1417], msg[1418], msg[1419], msg[1420], msg[1421], msg[1422], msg[1423], msg[1424], msg[1425], msg[1426], msg[1427], msg[1428], msg[1429], msg[1430], msg[1431], msg[1432], msg[1433], msg[1434], msg[1435], msg[1436], msg[1437], msg[1438], msg[1439], msg[1440], msg[1441], msg[1442], msg[1443], msg[1444], msg[1445], msg[1446], msg[1447], msg[1448], msg[1449], msg[1450], msg[1451], msg[1452], msg[1453], msg[1454], msg[1455], msg[1456], msg[1457], msg[1458], msg[1459], msg[1460], msg[1461], msg[1462], msg[1463], msg[1464], msg[1465], msg[1466], msg[1467], msg[1468], msg[1469], msg[1470], msg[1471], msg[1472], msg[1473], msg[1474], msg[1475], msg[1476], msg[1477], msg[1478], msg[1479], msg[1480], msg[1481], msg[1482], msg[1483], msg[1484], msg[1485], msg[1486], msg[1487], msg[1488], msg[1489], msg[1490], msg[1491], msg[1492], msg[1493], msg[1494], msg[1495], msg[1496], msg[1497], msg[1498], msg[1499]};
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_testItem", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String[] test(String tableName, int index, int count) {
        Map<Integer, String[]> map = getNameAll(tableName, index, count);
        String[] msg = new String[1500];
        for (int i = 0; i < map.size(); i++) {
            String[] value = (String[]) map.get(i);
            String itemId = value[2];
            String name = value[1];
            final L1Item item = ItemTable.get().getTemplate(Integer.parseInt(itemId));
            String str = itemId + "-" + name + "(" + item.getNameId() + ")";
            msg[i] = str;
        }
        return msg;
    }

    private Map<Integer, String[]> getNameAll(String tableName, int index, int count) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT item_id,name,name_id FROM " + tableName + " LIMIT " + index + "," + count);
            rs = pstm.executeQuery();
            int i = 0;
            while (rs.next()) {
                String nameId = rs.getString("name_id").substring(1);
                String name = rs.getString("name");
                int itemId = rs.getInt("item_id");
                String[] tmp = new String[3];
                tmp[0] = nameId;
                tmp[1] = name;
                tmp[2] = String.valueOf(itemId);
                map.put(i, tmp);
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return map;
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String s, final long amount) {
    }
}
