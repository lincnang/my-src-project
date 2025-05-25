package com.lineage.server.model;
//新增類別

public class ArrayExample {
    // 类属性中定义数组
    private int[] arr;

    // 构造函数初始化数组
    public ArrayExample(int size) {
        arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i + 1; // 初始化数组元素
        }
    }

    // 查询局部数组元素
    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println("Element at index " + i + ": " + arr[i]);
        }
    }

    // 修改局部数组元素
    public static void modifyArray(int[] arr, int index, int newValue) {
        if (index >= 0 && index < arr.length) {
            arr[index] = newValue;
        } else {
            System.out.println("Index out of bounds");
        }
    }

    public static void main(String[] args) {
        // 创建对象并初始化数组
        ArrayExample example = new ArrayExample(5);

        // 查询数组元素
        example.printArray();

        // 修改数组元素
        example.modifyArray(2, 100);

        // 查询修改后的数组元素
        example.printArray();

        // 在方法中定义和操作局部数组
        int[] arr = {10, 20, 30, 40, 50};
        printArray(arr);
        modifyArray(arr, 2, 200);
        printArray(arr);
    }

    // 查询数组元素
    public void printArray() {
        for (int i = 0; i < arr.length; i++) {
            System.out.println("Element at index " + i + ": " + arr[i]);
        }
    }

    // 修改数组元素
    public void modifyArray(int index, int newValue) {
        if (index >= 0 && index < arr.length) {
            arr[index] = newValue;
        } else {
            System.out.println("Index out of bounds");
        }
    }
}
