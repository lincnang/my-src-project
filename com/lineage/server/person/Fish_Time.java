package com.lineage.server.person;

import com.google.protobuf.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * 新封包-釣魚
 */
public final class Fish_Time {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor sendFishTime_descriptor = (Descriptors.Descriptor) getDescriptor().getMessageTypes().get(9);
    private static final GeneratedMessageV3.FieldAccessorTable sendFishTime_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(sendFishTime_descriptor, new String[]{"Type", "Time", "IsQuick"});

    static {
        String[] descriptorData = {"\n\022PBMessageALL.proto\022\031com.lineage.server.person\"\002\n\005type1\022\017\n\007value_1\030\001 \001(\005\022\017\n\007value_2\030\002 \001(\005\022\017\n\007value_3\030\003 \001(\005\022\017\n\007value_4\030\004 \001(\005\022\017\n\007value_5\030\005 \001(\005\022\017\n\007value_6\030\006 \001(\005\022\017\n\007value_7\030\007 \001(\005\022\017\n\007value_8\030\b \001(\005\022\017\n\007value_9\030\t \001(\005\022\020\n\bvalue_10\030\n \001(\005\022\020\n\bvalue_11\030\013 \001(\005\022\020\n\bvalue_12\030\f \001(\005\022\020\n\bvalue_13\030\r \001(\005\022\020\n\bvalue_14\030\016 \001(\005\022\020\n\bvalue_15\030\017 \001(\005\"\002\n\005type2\022\017\n\007value_1\030\001 \001(\005\022\017\n\007value_2\030\002 \001(\005\022\017\n\007array_3\030\003 \003(\f\022\017\n\007value_4\030\004 \001(\005\022\017\n\007", "value_5\030\005 \001(\005\022\017\n\007value_6\030\006 \001(\005\022\017\n\007value_7\030\007 \001(\005\022\017\n\007value_8\030\b \001(\005\022\017\n\007value_9\030\t \001(\005\022\020\n\bvalue_10\030\n \001(\005\022\020\n\bvalue_11\030\013 \001(\005\022\020\n\bvalue_12\030\f \001(\005\022\020\n\bvalue_13\030\r \001(\005\022\020\n\bvalue_14\030\016 \001(\005\022\020\n\bvalue_15\030\017 \001(\005\"\002\n\005type3\022\017\n\007value_1\030\001 \001(\005\022\017\n\007value_2\030\002 \001(\005\022\017\n\007value_3\030\003 \001(\005\022\017\n\007value_4\030\004 \001(\005\022\017\n\007value_5\030\005 \001(\005\022\017\n\007value_6\030\006 \001(\005\022\017\n\007value_7\030\007 \001(\005\022\017\n\007array_8\030\b \001(\f\022\017\n\007value_9\030\t \001(\005\022\020\n\bvalue_10\030\n \001(\005\022\020\n\bvalue_11\030\013 \001(\005\022\020\n\barray_12", "\030\f \001(\f\022\020\n\barray_13\030\r \001(\f\022\020\n\bvalue_14\030\016 \001(\005\022\020\n\bvalue_15\030\017 \001(\005\022\020\n\bvalue_16\030\020 \001(\005\"\002\n\005type4\022\017\n\007array_1\030\001 \001(\f\022\017\n\007array_2\030\002 \001(\f\022\017\n\007value_3\030\003 \001(\005\022\017\n\007value_4\030\004 \001(\005\022\017\n\007array_5\030\005 \001(\f\022\017\n\007array_6\030\006 \001(\f\022\017\n\007array_7\030\007 \001(\f\022\017\n\007value_8\030\b \001(\005\022\017\n\007value_9\030\t \001(\005\022\020\n\bvalue_10\030\n \001(\005\022\020\n\bvalue_11\030\013 \001(\005\022\020\n\bvalue_12\030\f \001(\005\022\020\n\bvalue_13\030\r \001(\005\022\020\n\bvalue_14\030\016 \001(\005\022\020\n\bvalue_15\030\017 \001(\005\"\002\n\005type5\022\017\n\007array_1\030\001 \001(\f\022\017\n\007array_2\030\002 \001(\f\022\017\n\007val", "ue_3\030\003 \001(\005\022\017\n\007value_4\030\004 \001(\005\022\017\n\007value_5\030\005 \001(\005\022\017\n\007value_6\030\006 \001(\005\022\017\n\007value_7\030\007 \001(\005\022\017\n\007value_8\030\b \001(\005\022\017\n\007value_9\030\t \001(\005\022\020\n\bvalue_10\030\n \001(\005\022\020\n\bvalue_11\030\013 \001(\005\022\020\n\bvalue_12\030\f \001(\005\022\020\n\bvalue_13\030\r \001(\005\022\020\n\bvalue_14\030\016 \001(\005\022\020\n\bvalue_15\030\017 \001(\005B)\n\031com.lineage.server.person.Fish_Time"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = root -> {
            Fish_Time.descriptor = root;
            return null;
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        registerAllExtensions(registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    public static abstract interface sendFishTimeOrBuilder extends MessageOrBuilder {
        public abstract boolean hasType();

        public abstract int getType();

        public abstract boolean hasTime();

        public abstract int getTime();

        public abstract boolean hasIsQuick();

        public abstract int getIsQuick();
    }

    public static final class sendFishTime extends GeneratedMessageV3 implements Fish_Time.sendFishTimeOrBuilder {
        public static final int TYPE_FIELD_NUMBER = 1;
        public static final int TIME_FIELD_NUMBER = 2;
        public static final int ISQUICK_FIELD_NUMBER = 3;
        @Deprecated
        public static final Parser<sendFishTime> PARSER = new AbstractParser<sendFishTime>() {
            public Fish_Time.sendFishTime parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Fish_Time.sendFishTime(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final sendFishTime DEFAULT_INSTANCE = new sendFishTime();
        private int bitField0_;
        private int type_;
        private int time_;
        private int isQuick_;
        private byte memoizedIsInitialized = -1;

        private sendFishTime(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private sendFishTime() {
            this.type_ = 0;
            this.time_ = 0;
            this.isQuick_ = 0;
        }

        private sendFishTime(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            @SuppressWarnings("unused") int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            this.bitField0_ |= 1;
                            this.type_ = input.readInt32();
                            break;
                        }
                        case 16: {
                            this.bitField0_ |= 2;
                            this.time_ = input.readInt32();
                            break;
                        }
                        case 24: {
                            this.bitField0_ |= 4;
                            this.isQuick_ = input.readInt32();
                            break;
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static Descriptors.Descriptor getDescriptor() {
            return Fish_Time.sendFishTime_descriptor;
        }

        public static sendFishTime parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (sendFishTime) PARSER.parseFrom(data);
        }

        public static sendFishTime parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendFishTime) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendFishTime parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (sendFishTime) PARSER.parseFrom(data);
        }

        public static sendFishTime parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendFishTime) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendFishTime parseFrom(InputStream input) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendFishTime parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendFishTime parseDelimitedFrom(InputStream input) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static sendFishTime parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendFishTime parseFrom(CodedInputStream input) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendFishTime parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendFishTime) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(sendFishTime prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static sendFishTime getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<sendFishTime> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Fish_Time.sendFishTime_fieldAccessorTable.ensureFieldAccessorsInitialized(sendFishTime.class, Builder.class);
        }

        public boolean hasType() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getType() {
            return this.type_;
        }

        public boolean hasTime() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public int getTime() {
            return this.time_;
        }

        public boolean hasIsQuick() {
            return (this.bitField0_ & 0x4) == 4;
        }

        public int getIsQuick() {
            return this.isQuick_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasType()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.type_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeInt32(2, this.time_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                output.writeInt32(3, this.isQuick_);
            }
            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 1) {
                size = size + CodedOutputStream.computeInt32Size(1, this.type_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeInt32Size(2, this.time_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                size = size + CodedOutputStream.computeInt32Size(3, this.isQuick_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof sendFishTime)) {
                return super.equals(obj);
            }
            sendFishTime other = (sendFishTime) obj;
            boolean result = true;
            result = (result) && (hasType() == other.hasType());
            if (hasType()) {
                result = (result) && (getType() == other.getType());
            }
            result = (result) && (hasTime() == other.hasTime());
            if (hasTime()) {
                result = (result) && (getTime() == other.getTime());
            }
            result = (result) && (hasIsQuick() == other.hasIsQuick());
            if (hasIsQuick()) {
                result = (result) && (getIsQuick() == other.getIsQuick());
            }
            result = (result) && (this.unknownFields.equals(other.unknownFields));
            return result;
        }

        @SuppressWarnings("unchecked")
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + getDescriptorForType().hashCode();
            if (hasType()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getType();
            }
            if (hasTime()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getTime();
            }
            if (hasIsQuick()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + getIsQuick();
            }
            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder(null) : new Builder(null).mergeFrom(this);
        }

        protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public Parser<sendFishTime> getParserForType() {
            return PARSER;
        }

        public sendFishTime getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Fish_Time.sendFishTimeOrBuilder {
            private int bitField0_;
            private int type_;
            private int time_;
            private int isQuick_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return Fish_Time.sendFishTime_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Fish_Time.sendFishTime_fieldAccessorTable.ensureFieldAccessorsInitialized(Fish_Time.sendFishTime.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
            }

            public Builder clear() {
                super.clear();
                this.type_ = 0;
                this.bitField0_ &= -2;
                this.time_ = 0;
                this.bitField0_ &= -3;
                this.isQuick_ = 0;
                this.bitField0_ &= -5;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return Fish_Time.sendFishTime_descriptor;
            }

            public Fish_Time.sendFishTime getDefaultInstanceForType() {
                return Fish_Time.sendFishTime.getDefaultInstance();
            }

            public Fish_Time.sendFishTime build() {
                Fish_Time.sendFishTime result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public Fish_Time.sendFishTime buildPartial() {
                Fish_Time.sendFishTime result = new Fish_Time.sendFishTime(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.type_ = this.type_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                result.time_ = this.time_;
                if ((from_bitField0_ & 0x4) == 4) {
                    to_bitField0_ |= 4;
                }
                result.isQuick_ = this.isQuick_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }

            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(Message other) {
                if ((other instanceof Fish_Time.sendFishTime)) {
                    return mergeFrom((Fish_Time.sendFishTime) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Fish_Time.sendFishTime other) {
                if (other == Fish_Time.sendFishTime.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    setType(other.getType());
                }
                if (other.hasTime()) {
                    setTime(other.getTime());
                }
                if (other.hasIsQuick()) {
                    setIsQuick(other.getIsQuick());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                if (!hasType()) {
                    return false;
                }
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Fish_Time.sendFishTime parsedMessage = null;
                try {
                    parsedMessage = (Fish_Time.sendFishTime) Fish_Time.sendFishTime.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Fish_Time.sendFishTime) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasType() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getType() {
                return this.type_;
            }

            public Builder setType(int value) {
                this.bitField0_ |= 1;
                this.type_ = value;
                onChanged();
                return this;
            }

            public Builder clearType() {
                this.bitField0_ &= -2;
                this.type_ = 0;
                onChanged();
                return this;
            }

            public boolean hasTime() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getTime() {
                return this.time_;
            }

            public Builder setTime(int value) {
                this.bitField0_ |= 2;
                this.time_ = value;
                onChanged();
                return this;
            }

            public Builder clearTime() {
                this.bitField0_ &= -3;
                this.time_ = 0;
                onChanged();
                return this;
            }

            public boolean hasIsQuick() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getIsQuick() {
                return this.isQuick_;
            }

            public Builder setIsQuick(int value) {
                this.bitField0_ |= 4;
                this.isQuick_ = value;
                onChanged();
                return this;
            }

            public Builder clearIsQuick() {
                this.bitField0_ &= -5;
                this.isQuick_ = 0;
                onChanged();
                return this;
            }

            public Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }
    }
}
