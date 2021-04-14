package com.ns.design.pattern.flyweight;

/**
 * @author ns
 * @date 2021/3/30  18:16
 */
public class TestString {

	public static void main(String[] args) {
		String s1 = "hello";
		String s2 = "hello";
		String s3 = "he" + "llo";
		String s4 = "he" + new String("llo");
		String s5 = new String("hello");
		String s6 = s5.intern();
		String s7 = "he";
		String s8 = "llo";
		String s9 = s7 + s8;
		System.out.println(s1 == s2); 	// true 因为在编译阶段就已经在字符串常量池中创建好 "hello"对象，s1和s2指向同一个引用
		System.out.println(s1 == s3); 	// true 如果是两个字面量拼接，在编译阶段就已经拼接好了
		System.out.println(s1 == s4); 	// false s4 为一个字面量和对象的相加，会生成一个新的对象
		System.out.println(s1 == s9); 	// false s9为两个对象相加，会生成一个新的对象
		System.out.println(s4 == s5); 	// false s4和s5都会生成一个新的对象
		System.out.println(s1 == s6); 	// true s5.intern()会使位于堆中的字符串在运行阶段动态的加入字符串常量池中，如果字符串
										// 常量池中已经有该字面量，则会返回该字面量的引用
	}
}
