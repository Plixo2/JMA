package org.karina.model.model.bytecode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public sealed interface Instruction {
    default int opcode() {
        return 0x00;
    }



    record NOP() implements Instruction { }


    record PushNull() implements Instruction {}
    record PushByte(byte value) implements Instruction {}
    record PushShort(short value) implements Instruction {}
    record PushInt(int value) implements Instruction {}
    record PushLong(long value) implements Instruction {}
    record PushFloat(float value) implements Instruction {}
    record PushDouble(double value) implements Instruction {}
    record PushString(String value) implements Instruction {}
    record PushClass(String className) implements Instruction {}
    record PushMethodType(MethodType type) implements Instruction {}
    record PushMethodHandle(MethodHandle handle) implements Instruction {}


    record LoadVariable(byte variable) implements Instruction {}
    record StoreVariable(byte variable) implements Instruction {}
    record LoadArray() implements Instruction {}
    record StoreArray() implements Instruction {}

    record Pop() implements Instruction {}
    record Duplicate() implements Instruction {}
    record Swap() implements Instruction {}



}
