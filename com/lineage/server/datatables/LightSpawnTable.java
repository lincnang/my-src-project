package com.lineage.server.datatables;

public class LightSpawnTable {
    private static LightSpawnTable _instance;

    private LightSpawnTable() {
        FillLightSpawnTable();
    }

    public static LightSpawnTable getInstance() {
        if (_instance == null) {
            _instance = new LightSpawnTable();
        }
        return _instance;
    }

    // ERROR //
    private void FillLightSpawnTable() {
        // Byte code:
        // 0: new 35 com/lineage/server/utils/PerformanceTimer
        // 3: dup
        // 4: invokespecial 37 com/lineage/server/utils/PerformanceTimer:<init>
        // ()V
        // 7: astore_1
        // 8: iconst_0
        // 9: istore_2
        // 10: aconst_null
        // 11: astore_3
        // 12: aconst_null
        // 13: astore 4
        // 15: aconst_null
        // 16: astore 5
        // 18: invokestatic 38 com/lineage/DatabaseFactory:get
        // ()Lcom/lineage/DatabaseFactory;
        // 21: invokevirtual 44 com/lineage/DatabaseFactory:getConnection
        // ()Ljava/sql/Connection;
        // 24: astore_3
        // 25: aload_3
        // 26: ldc 48
        // 28: invokeinterface 50 2 0
        // 33: astore 4
        // 35: aload 4
        // 37: invokeinterface 56 1 0
        // 42: astore 5
        // 44: aload 5
        // 46: invokeinterface 62 1 0
        // 51: ifne +6 -> 57
        // 54: goto +219 -> 273
        // 57: ldc2_w 68
        // 60: invokestatic 70 java/lang/Thread:sleep (J)V
        // 63: aload 5
        // 65: ldc 76
        // 67: invokeinterface 78 2 0
        // 72: istore 6
        // 74: iinc 2 1
        // 77: invokestatic 82 com/lineage/server/datatables/NpcTable:get
        // ()Lcom/lineage/server/datatables/NpcTable;
        // 80: iload 6
        // 82: invokevirtual 87
        // com/lineage/server/datatables/NpcTable:getTemplate
        // (I)Lcom/lineage/server/templates/L1Npc;
        // 85: astore 7
        // 87: aload 7
        // 89: ifnull -45 -> 44
        // 92: invokestatic 82 com/lineage/server/datatables/NpcTable:get
        // ()Lcom/lineage/server/datatables/NpcTable;
        // 95: iload 6
        // 97: invokevirtual 91
        // com/lineage/server/datatables/NpcTable:newNpcInstance
        // (I)Lcom/lineage/server/model/Instance/L1NpcInstance;
        // 100: checkcast 95
        // com/lineage/server/model/Instance/L1FieldObjectInstance
        // 103: astore 8
        // 105: aload 8
        // 107: invokestatic 97 com/lineage/server/IdFactoryNpc:get
        // ()Lcom/lineage/server/IdFactoryNpc;
        // 110: invokevirtual 102 com/lineage/server/IdFactoryNpc:nextId ()I
        // 113: invokevirtual 106
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setId (I)V
        // 116: aload 8
        // 118: aload 5
        // 120: ldc 110
        // 122: invokeinterface 78 2 0
        // 127: invokevirtual 112
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setX (I)V
        // 130: aload 8
        // 132: aload 5
        // 134: ldc 115
        // 136: invokeinterface 78 2 0
        // 141: invokevirtual 117
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setY (I)V
        // 144: aload 8
        // 146: aload 5
        // 148: ldc 120
        // 150: invokeinterface 78 2 0
        // 155: i2s
        // 156: invokevirtual 122
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setMap (S)V
        // 159: aload 8
        // 161: aload 8
        // 163: invokevirtual 126
        // com/lineage/server/model/Instance/L1FieldObjectInstance:getX ()I
        // 166: invokevirtual 129
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setHomeX (I)V
        // 169: aload 8
        // 171: aload 8
        // 173: invokevirtual 132
        // com/lineage/server/model/Instance/L1FieldObjectInstance:getY ()I
        // 176: invokevirtual 135
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setHomeY (I)V
        // 179: aload 8
        // 181: iconst_0
        // 182: invokevirtual 138
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setHeading
        // (I)V
        // 185: aload 8
        // 187: aload 7
        // 189: invokevirtual 141
        // com/lineage/server/templates/L1Npc:getLightSize ()I
        // 192: invokevirtual 146
        // com/lineage/server/model/Instance/L1FieldObjectInstance:setLightSize
        // (I)V
        // 195: invokestatic 149 com/lineage/server/world/World:get
        // ()Lcom/lineage/server/world/World;
        // 198: aload 8
        // 200: invokevirtual 154 com/lineage/server/world/World:storeObject
        // (Lcom/lineage/server/model/L1Object;)V
        // 203: invokestatic 149 com/lineage/server/world/World:get
        // ()Lcom/lineage/server/world/World;
        // 206: aload 8
        // 208: invokevirtual 158
        // com/lineage/server/world/World:addVisibleObject
        // (Lcom/lineage/server/model/L1Object;)V
        // 211: goto -167 -> 44
        // 214: astore 6
        // 216: getstatic 18 com/lineage/server/datatables/LightSpawnTable:_log
        // Lorg/apache/commons/logging/Log;
        // 219: aload 6
        // 221: invokevirtual 161 java/lang/Exception:getLocalizedMessage
        // ()Ljava/lang/String;
        // 224: aload 6
        // 226: invokeinterface 167 3 0
        // 231: aload 5
        // 233: invokestatic 173 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/ResultSet;)Ljava/sql/SQLException;
        // 236: pop
        // 237: aload 4
        // 239: invokestatic 179 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Statement;)Ljava/sql/SQLException;
        // 242: pop
        // 243: aload_3
        // 244: invokestatic 182 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Connection;)Ljava/sql/SQLException;
        // 247: pop
        // 248: goto +42 -> 290
        // 251: astore 9
        // 253: aload 5
        // 255: invokestatic 173 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/ResultSet;)Ljava/sql/SQLException;
        // 258: pop
        // 259: aload 4
        // 261: invokestatic 179 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Statement;)Ljava/sql/SQLException;
        // 264: pop
        // 265: aload_3
        // 266: invokestatic 182 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Connection;)Ljava/sql/SQLException;
        // 269: pop
        // 270: aload 9
        // 272: athrow
        // 273: aload 5
        // 275: invokestatic 173 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/ResultSet;)Ljava/sql/SQLException;
        // 278: pop
        // 279: aload 4
        // 281: invokestatic 179 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Statement;)Ljava/sql/SQLException;
        // 284: pop
        // 285: aload_3
        // 286: invokestatic 182 com/lineage/server/utils/SQLUtil:close
        // (Ljava/sql/Connection;)Ljava/sql/SQLException;
        // 289: pop
        // 290: getstatic 18 com/lineage/server/datatables/LightSpawnTable:_log
        // Lorg/apache/commons/logging/Log;
        // 293: new 185 java/lang/StringBuilder
        // 296: dup
        // 297: ldc 187
        // 299: invokespecial 189 java/lang/StringBuilder:<init>
        // (Ljava/lang/String;)V
        // 302: iload_2
        // 303: invokevirtual 192 java/lang/StringBuilder:append
        // (I)Ljava/lang/StringBuilder;
        // 306: ldc 196
        // 308: invokevirtual 198 java/lang/StringBuilder:append
        // (Ljava/lang/String;)Ljava/lang/StringBuilder;
        // 311: aload_1
        // 312: invokevirtual 201 com/lineage/server/utils/PerformanceTimer:get
        // ()J
        // 315: invokevirtual 204 java/lang/StringBuilder:append
        // (J)Ljava/lang/StringBuilder;
        // 318: ldc 207
        // 320: invokevirtual 198 java/lang/StringBuilder:append
        // (Ljava/lang/String;)Ljava/lang/StringBuilder;
        // 323: invokevirtual 209 java/lang/StringBuilder:toString
        // ()Ljava/lang/String;
        // 326: invokeinterface 212 2 0
        // 331: return
        //
        // Exception table:
        // from to target type
        // 18 214 214 java/lang/Exception
        // 18 231 251 finally
    }
}