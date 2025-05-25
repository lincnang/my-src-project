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

 public class InvalidProtocolBufferException extends IOException {
     private static final long serialVersionUID = -1616151763072450476L;

     public InvalidProtocolBufferException(String description) {
         super(description);
     }

     static InvalidProtocolBufferException truncatedMessage() {
         return new InvalidProtocolBufferException("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
     }

     static InvalidProtocolBufferException negativeSize() {
         return new InvalidProtocolBufferException("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
     }

     static InvalidProtocolBufferException malformedVarint() {
         return new InvalidProtocolBufferException("CodedInputStream encountered a malformed varint.");
     }

     static InvalidProtocolBufferException invalidTag() {
         return new InvalidProtocolBufferException("Protocol message contained an invalid tag (zero).");
     }

     static InvalidProtocolBufferException invalidEndTag() {
         return new InvalidProtocolBufferException("Protocol message end-group tag did not match expected tag.");
     }

     static InvalidProtocolBufferException invalidWireType() {
         return new InvalidProtocolBufferException("Protocol message tag had invalid wire type.");
     }

     static InvalidProtocolBufferException recursionLimitExceeded() {
         return new InvalidProtocolBufferException("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
     }

     static InvalidProtocolBufferException sizeLimitExceeded() {
         return new InvalidProtocolBufferException("Protocol message was too large.  May be malicious.  Use CodedInputStream.setSizeLimit() to increase the size limit.");
     }

     static InvalidProtocolBufferException parseFailure() {
         return new InvalidProtocolBufferException("Failed to parse the message.");
     }

     static InvalidProtocolBufferException badWriteType(String string) {
         return new InvalidProtocolBufferException("非法屬性:" + string);
     }
 }