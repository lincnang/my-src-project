 /*
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2, or (at your option)
  * any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
  * 02111-1307, USA.
  *
  * http://www.gnu.org/copyleft/gpl.html
  */
 package com.lineage.server.utils;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

 /**
  * @**抽抽樂**
  * @作者:冰雕寵兒
  */
 public final class CodedInputStream {
     static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
     static final int TAG_TYPE_BITS = 3;
     static final int TAG_TYPE_MASK = 7;
     static final int WIRETYPE_END_GROUP = 4;
     static final int WIRETYPE_FIXED32 = 5;
     static final int WIRETYPE_FIXED64 = 1;
     static final int WIRETYPE_LENGTH_DELIMITED = 2;
     static final int WIRETYPE_START_GROUP = 3;
     static final int WIRETYPE_VARINT = 0;
     private static final int BUFFER_SIZE = 4096;
     private byte[] buffer;
     private int bufferPos;
     private int bufferSize;
     private int bufferSizeAfterLimit;
     private int currentLimit = Integer.MAX_VALUE;
     private int lastTag;
     private int totalBytesRetired;

     private CodedInputStream(byte[] buffer, int off, int len) {
         this.buffer = buffer;
         this.bufferSize = off + len;
         this.bufferPos = off;
         this.totalBytesRetired = -off;
     }

     public static CodedInputStream newInstance(byte[] buf) {
         return newInstance(buf, 0, buf.length);
     }

     public static CodedInputStream newInstance(byte[] buf, int off, int len) {
         CodedInputStream result = new CodedInputStream(buf, off, len);
         try {
             result.pushLimit(len);
             return result;
         } catch (InvalidProtocolBufferException ex) {
             throw new IllegalArgumentException(ex);
         }
     }

     public static int decodeZigZag32(int n) {
         return (n >>> WIRETYPE_FIXED64) ^ (-(n & WIRETYPE_FIXED64));
     }

     public static long decodeZigZag64(long n) {
         return (n >>> WIRETYPE_FIXED64) ^ (-(1 & n));
     }

     public static int getTagWireType(int tag) {
         return tag & TAG_TYPE_MASK;
     }

     public static int getTagFieldNumber(int tag) {
         return tag >>> 3;
     }

     static int makeTag(int fieldNumber, int wireType) {
         return (fieldNumber << 3) | wireType;
     }

     private static String bytesToHex(byte[] byteArray) {
         StringBuilder sb = new StringBuilder();
         String stmp = "";
         for (int n = 0; n < byteArray.length; n += WIRETYPE_FIXED64) {
             stmp = Integer.toHexString(byteArray[n] & 255);
             if (stmp.length() == WIRETYPE_FIXED64) {
                 sb.append("0").append(stmp);
             } else {
                 sb.append(stmp);
             }
             if (n < byteArray.length - 1) {
                 sb.append(":");
             }
         }
         return sb.toString();
     }

     public boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
         int number = getTagFieldNumber(tag);
         switch (getTagWireType(tag)) {
             case 0:
                 System.out.println("未知屬性 Key:" + number + " Value:" + input.readInt64());
                 break;
             case WIRETYPE_FIXED64 /*1*/:
                 System.out.println("未知屬性 Key:" + number + " Value:" + input.readFixed64());
                 break;
             case WIRETYPE_LENGTH_DELIMITED /*2*/:
                 System.out.println("未知屬性 Key:" + number + " Value:" + bytesToHex(input.readByteArray()).toUpperCase().replace(":", " "));
                 break;
             case 3:
                 throw InvalidProtocolBufferException.badWriteType("START_GROUP");
             case WIRETYPE_END_GROUP /*4*/:
                 throw InvalidProtocolBufferException.badWriteType("END_GROUP");
             case WIRETYPE_FIXED32 /*5*/:
                 System.out.println("未知屬性 Key:" + number + " Value:" + input.readFixed32());
                 break;
             default:
                 throw InvalidProtocolBufferException.invalidWireType();
         }
         return true;
     }

     public int readTag() throws IOException {
         if (isAtEnd()) {
             this.lastTag = 0;
             return 0;
         }
         this.lastTag = readRawVarint32();
         if (getTagFieldNumber(this.lastTag) != 0) {
             return this.lastTag;
         }
         throw InvalidProtocolBufferException.invalidTag();
     }

     public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
             throw InvalidProtocolBufferException.invalidEndTag();
         }
     }

     public int getLastTag() {
         return this.lastTag;
     }

     public boolean skipField(int tag) throws IOException {
         switch (getTagWireType(tag)) {
             case 0:
                 skipRawVarint();
                 return true;
             case WIRETYPE_FIXED64 /*1*/:
                 skipRawBytes(8);
                 return true;
             case WIRETYPE_LENGTH_DELIMITED /*2*/:
                 skipRawBytes(readRawVarint32());
                 return true;
             case 3:
                 skipMessage();
                 checkLastTagWas(makeTag(getTagFieldNumber(tag), WIRETYPE_END_GROUP));
                 return true;
             case WIRETYPE_END_GROUP /*4*/:
                 return false;
             case WIRETYPE_FIXED32 /*5*/:
                 skipRawBytes(WIRETYPE_END_GROUP);
                 return true;
             default:
                 throw InvalidProtocolBufferException.invalidWireType();
         }
     }

     public void skipMessage() throws IOException {
         int tag;
         do {
             tag = readTag();
             if (tag == 0) {
                 return;
             }
         } while (skipField(tag));
     }

     public double readDouble() throws IOException {
         return Double.longBitsToDouble(readRawLittleEndian64());
     }

     public float readFloat() throws IOException {
         return Float.intBitsToFloat(readRawLittleEndian32());
     }

     public long readUInt64() throws IOException {
         return readRawVarint64();
     }

     public long readInt64() throws IOException {
         return readRawVarint64();
     }

     public int readInt32() throws IOException {
         return readRawVarint32();
     }

     public long readFixed64() throws IOException {
         return readRawLittleEndian64();
     }

     public int readFixed32() throws IOException {
         return readRawLittleEndian32();
     }

     public boolean readBool() throws IOException {
         return readRawVarint64() != 0;
     }

     public String readString(String charsetName) throws IOException {
         int size = readRawVarint32();
         if (size <= this.bufferSize - this.bufferPos && size > 0) {
             String result = new String(this.buffer, this.bufferPos, size, charsetName);
             this.bufferPos += size;
             return result;
         } else if (size == 0) {
             return "";
         } else {
             return new String(readRawBytesSlowPath(size), charsetName);
         }
     }

     public byte[] readByteArray() throws IOException {
         int size = readRawVarint32();
         if (size > this.bufferSize - this.bufferPos || size <= 0) {
             return readRawBytesSlowPath(size);
         }
         byte[] result = Arrays.copyOfRange(this.buffer, this.bufferPos, this.bufferPos + size);
         this.bufferPos += size;
         return result;
     }

     public int readUInt32() throws IOException {
         return readRawVarint32();
     }

     public int readEnum() throws IOException {
         return readRawVarint32();
     }

     public int readSFixed32() throws IOException {
         return readRawLittleEndian32();
     }

     public long readSFixed64() throws IOException {
         return readRawLittleEndian64();
     }

     public int readSInt32() throws IOException {
         return decodeZigZag32(readRawVarint32());
     }

     public long readSInt64() throws IOException {
         return decodeZigZag64(readRawVarint64());
     }

     public int readRawVarint32() throws IOException {
         int pos = this.bufferPos;
         if (this.bufferSize != pos) {
             byte[] buffer = this.buffer;
             int pos2 = pos + WIRETYPE_FIXED64;
             int x = buffer[pos];
             if (x >= 0) {
                 this.bufferPos = pos2;
                 pos = pos2;
                 return x;
             } else if (this.bufferSize - pos2 < 9) {
                 pos = pos2;
             } else {
                 pos = pos2 + WIRETYPE_FIXED64;
                 x ^= buffer[pos2] << TAG_TYPE_MASK;
                 if (((long) x) < 0) {
                     x = (int) (((long) x) ^ -128);
                 } else {
                     pos2 = pos + WIRETYPE_FIXED64;
                     x ^= buffer[pos] << 14;
                     if (((long) x) >= 0) {
                         x = (int) (((long) x) ^ 16256);
                         pos = pos2;
                     } else {
                         pos = pos2 + WIRETYPE_FIXED64;
                         x ^= buffer[pos2] << 21;
                         if (((long) x) < 0) {
                             x = (int) (((long) x) ^ -2080896);
                         } else {
                             pos2 = pos + WIRETYPE_FIXED64;
                             int y = buffer[pos];
                             x = (int) (((long) (x ^ ((long) y << 28))) ^ 266354560);
                             if (y < 0) {
                                 pos = pos2 + WIRETYPE_FIXED64;
                                 if (buffer[pos2] < (byte) 0) {
                                     pos2 = pos + WIRETYPE_FIXED64;
                                     if (buffer[pos] < (byte) 0) {
                                         pos = pos2 + WIRETYPE_FIXED64;
                                         if (buffer[pos2] < (byte) 0) {
                                             pos2 = pos + WIRETYPE_FIXED64;
                                             if (buffer[pos] < (byte) 0) {
                                                 pos = pos2 + WIRETYPE_FIXED64;
                                                 if (buffer[pos2] < (byte) 0) {
                                                 }
                                             }
                                         }
                                     }
                                 }
                             }
                             pos = pos2;
                         }
                     }
                 }
                 this.bufferPos = pos;
                 return x;
             }
         }
         return (int) readRawVarint64SlowPath();
     }

     private void skipRawVarint() throws IOException {
         if (this.bufferSize - this.bufferPos >= 10) {
             byte[] buffer = this.buffer;
             int i = 0;
             int pos = this.bufferPos;
             while (i < 10) {
                 int pos2 = pos + WIRETYPE_FIXED64;
                 if (buffer[pos] >= (byte) 0) {
                     this.bufferPos = pos2;
                     return;
                 } else {
                     i += WIRETYPE_FIXED64;
                     pos = pos2;
                 }
             }
         }
         skipRawVarintSlowPath();
     }

     private void skipRawVarintSlowPath() throws IOException {
         int i = 0;
         while (i < 10) {
             if (readRawByte() < (byte) 0) {
                 i += WIRETYPE_FIXED64;
             } else {
                 return;
             }
         }
         throw InvalidProtocolBufferException.malformedVarint();
     }

     public long readRawVarint64() throws IOException {
         int pos = this.bufferPos;
         if (this.bufferSize != pos) {
             byte[] buffer = this.buffer;
             int pos2 = pos + WIRETYPE_FIXED64;
             int y = buffer[pos];
             if (y >= 0) {
                 this.bufferPos = pos2;
                 pos = pos2;
                 return (long) y;
             } else if (this.bufferSize - pos2 < 9) {
                 pos = pos2;
             } else {
                 pos = pos2 + WIRETYPE_FIXED64;
                 long x = (long) ((buffer[pos2] << TAG_TYPE_MASK) ^ y);
                 if (x < 0) {
                     x ^= -128;
                 } else {
                     pos2 = pos + WIRETYPE_FIXED64;
                     x ^= (long) (buffer[pos] << 14);
                     if (x >= 0) {
                         x ^= 16256;
                         pos = pos2;
                     } else {
                         pos = pos2 + WIRETYPE_FIXED64;
                         x ^= (long) (buffer[pos2] << 21);
                         if (x < 0) {
                             x ^= -2080896;
                         } else {
                             pos2 = pos + WIRETYPE_FIXED64;
                             x ^= ((long) buffer[pos]) << 28;
                             if (x >= 0) {
                                 x ^= 266354560;
                                 pos = pos2;
                             } else {
                                 pos = pos2 + WIRETYPE_FIXED64;
                                 x ^= ((long) buffer[pos2]) << 35;
                                 if (x < 0) {
                                     x ^= -34093383808L;
                                 } else {
                                     pos2 = pos + WIRETYPE_FIXED64;
                                     x ^= ((long) buffer[pos]) << 42;
                                     if (x >= 0) {
                                         x ^= 4363953127296L;
                                         pos = pos2;
                                     } else {
                                         pos = pos2 + WIRETYPE_FIXED64;
                                         x ^= ((long) buffer[pos2]) << 49;
                                         if (x < 0) {
                                             x ^= -558586000294016L;
                                         } else {
                                             pos2 = pos + WIRETYPE_FIXED64;
                                             x = (x ^ (((long) buffer[pos]) << 56)) ^ 71499008037633920L;
                                             if (x < 0) {
                                                 pos = pos2 + WIRETYPE_FIXED64;
                                                 if (((long) buffer[pos2]) < 0) {
                                                 }
                                             } else {
                                                 pos = pos2;
                                             }
                                         }
                                     }
                                 }
                             }
                         }
                     }
                 }
                 this.bufferPos = pos;
                 return x;
             }
         }
         return readRawVarint64SlowPath();
     }

     long readRawVarint64SlowPath() throws IOException {
         long result = 0;
         for (int shift = 0; shift < 64; shift += TAG_TYPE_MASK) {
             byte b = readRawByte();
             result |= ((long) (b & 127)) << shift;
             if ((b & 128) == 0) {
                 return result;
             }
         }
         throw InvalidProtocolBufferException.malformedVarint();
     }

     public int readRawLittleEndian32() throws IOException {
         int pos = this.bufferPos;
         if (this.bufferSize - pos < WIRETYPE_END_GROUP) {
             refillBuffer(WIRETYPE_END_GROUP);
             pos = this.bufferPos;
         }
         byte[] buffer = this.buffer;
         this.bufferPos = pos + WIRETYPE_END_GROUP;
         return (((buffer[pos] & 255) | ((buffer[pos + WIRETYPE_FIXED64] & 255) << 8)) | ((buffer[pos + WIRETYPE_LENGTH_DELIMITED] & 255) << 16)) | ((buffer[pos + 3] & 255) << 24);
     }

     public long readRawLittleEndian64() throws IOException {
         int pos = this.bufferPos;
         if (this.bufferSize - pos < 8) {
             refillBuffer(8);
             pos = this.bufferPos;
         }
         byte[] buffer = this.buffer;
         this.bufferPos = pos + 8;
         return (((((((((long) buffer[pos]) & 255) | ((((long) buffer[pos + WIRETYPE_FIXED64]) & 255) << 8)) | ((((long) buffer[pos + WIRETYPE_LENGTH_DELIMITED]) & 255) << 16)) | ((((long) buffer[pos + 3]) & 255) << 24)) | ((((long) buffer[pos + WIRETYPE_END_GROUP]) & 255) << 32)) | ((((long) buffer[pos + WIRETYPE_FIXED32]) & 255) << 40)) | ((((long) buffer[pos + 6]) & 255) << 48)) | ((((long) buffer[pos + TAG_TYPE_MASK]) & 255) << 56);
     }

     public void reset(byte[] buf) {
         reset(buf, 0, buf.length);
     }

     public void reset(byte[] buffer, int off, int len) {
         this.buffer = buffer;
         this.bufferSize = off + len;
         this.bufferPos = off;
         this.totalBytesRetired = -off;
         this.bufferSizeAfterLimit = 0;
         this.currentLimit = Integer.MAX_VALUE;
         this.lastTag = 0;
         try {
             pushLimit(len);
         } catch (InvalidProtocolBufferException ex) {
             throw new IllegalArgumentException(ex);
         }
     }

     public void resetSizeCounter() {
         this.totalBytesRetired = -this.bufferPos;
     }

     public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
             throw InvalidProtocolBufferException.negativeSize();
         }
         byteLimit += this.totalBytesRetired + this.bufferPos;
         int oldLimit = this.currentLimit;
         if (byteLimit > oldLimit) {
             throw InvalidProtocolBufferException.truncatedMessage();
         }
         this.currentLimit = byteLimit;
         recomputeBufferSizeAfterLimit();
         return oldLimit;
     }

     private void recomputeBufferSizeAfterLimit() {
         this.bufferSize += this.bufferSizeAfterLimit;
         int bufferEnd = this.totalBytesRetired + this.bufferSize;
         if (bufferEnd > this.currentLimit) {
             this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
             this.bufferSize -= this.bufferSizeAfterLimit;
             return;
         }
         this.bufferSizeAfterLimit = 0;
     }

     public void popLimit(int oldLimit) {
         this.currentLimit = oldLimit;
         recomputeBufferSizeAfterLimit();
     }

     public int getBytesUntilLimit() {
         if (this.currentLimit == Integer.MAX_VALUE) {
             return -1;
         }
         return this.currentLimit - (this.totalBytesRetired + this.bufferPos);
     }

     public boolean isAtEnd() throws IOException {
         return this.bufferPos == this.bufferSize && !tryRefillBuffer(WIRETYPE_FIXED64);
     }

     public int getTotalBytesRead() {
         return this.totalBytesRetired + this.bufferPos;
     }

     private void ensureAvailable(int n) throws IOException {
         if (this.bufferSize - this.bufferPos < n) {
             refillBuffer(n);
         }
     }

     private void refillBuffer(int n) throws IOException {
         if (!tryRefillBuffer(n)) {
             throw InvalidProtocolBufferException.truncatedMessage();
         }
     }

     private boolean tryRefillBuffer(int n) throws IOException {
         if (this.bufferPos + n > this.bufferSize) {
             return (this.totalBytesRetired + this.bufferPos) + n > this.currentLimit ? false : false;
         } else {
             throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
         }
     }

     public byte readRawByte() throws IOException {
         if (this.bufferPos == this.bufferSize) {
             refillBuffer(WIRETYPE_FIXED64);
         }
         byte[] bArr = this.buffer;
         int i = this.bufferPos;
         this.bufferPos = i + WIRETYPE_FIXED64;
         return bArr[i];
     }

     public byte[] readRawBytes(int size) throws IOException {
         int pos = this.bufferPos;
         if (size > this.bufferSize - pos || size <= 0) {
             return readRawBytesSlowPath(size);
         }
         this.bufferPos = pos + size;
         return Arrays.copyOfRange(this.buffer, pos, pos + size);
     }

     private byte[] readRawBytesSlowPath(int size) throws IOException {
         if (size <= 0) {
             if (size == 0) {
                 return EMPTY_BYTE_ARRAY;
             }
             throw InvalidProtocolBufferException.negativeSize();
         } else if ((this.totalBytesRetired + this.bufferPos) + size > this.currentLimit) {
             skipRawBytes((this.currentLimit - this.totalBytesRetired) - this.bufferPos);
             throw InvalidProtocolBufferException.truncatedMessage();
         } else if (size < BUFFER_SIZE) {
             final byte[] bytes = new byte[size];
             final int pos = this.bufferSize - this.bufferPos;
             System.arraycopy(this.buffer, this.bufferPos, bytes, 0, pos);
             this.bufferPos = this.bufferSize;
             ensureAvailable(size - pos);
             System.arraycopy(this.buffer, 0, bytes, pos, size - pos);
             this.bufferPos = size - pos;
             return bytes;
         } else {
             byte[] chunk;
             int originalBufferPos = this.bufferPos;
             int originalBufferSize = this.bufferSize;
             this.totalBytesRetired += this.bufferSize;
             this.bufferPos = 0;
             this.bufferSize = 0;
             int sizeLeft = size - (originalBufferSize - originalBufferPos);
             List<byte[]> chunks = new ArrayList<>();
             while (sizeLeft > 0) {
                 chunk = new byte[Math.min(sizeLeft, BUFFER_SIZE)];
                 if (chunk.length > 0) {
                     throw InvalidProtocolBufferException.truncatedMessage();
                 }
                 sizeLeft -= chunk.length;
                 chunks.add(chunk);
             }
             final byte[] bytes = new byte[size];
             int pos = originalBufferSize - originalBufferPos;
             System.arraycopy(this.buffer, originalBufferPos, bytes, 0, pos);
             for (byte[] chunk2 : chunks) {
                 System.arraycopy(chunk2, 0, bytes, pos, chunk2.length);
                 pos += chunk2.length;
             }
             return bytes;
         }
     }

     public void skipRawBytes(int size) throws IOException {
         if (size > this.bufferSize - this.bufferPos || size < 0) {
             skipRawBytesSlowPath(size);
         } else {
             this.bufferPos += size;
         }
     }

     private void skipRawBytesSlowPath(int size) throws IOException {
         if (size < 0) {
             throw InvalidProtocolBufferException.negativeSize();
         } else if ((this.totalBytesRetired + this.bufferPos) + size > this.currentLimit) {
             skipRawBytes((this.currentLimit - this.totalBytesRetired) - this.bufferPos);
             throw InvalidProtocolBufferException.truncatedMessage();
         } else {
             int pos = this.bufferSize - this.bufferPos;
             this.bufferPos = this.bufferSize;
             refillBuffer(WIRETYPE_FIXED64);
             while (size - pos > this.bufferSize) {
                 pos += this.bufferSize;
                 this.bufferPos = this.bufferSize;
                 refillBuffer(WIRETYPE_FIXED64);
             }
             this.bufferPos = size - pos;
         }
     }
 }