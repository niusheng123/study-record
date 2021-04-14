package com.ns.design.pattern.adapter;

import com.ns.design.pattern.adapter.objectadapter.ICBCLoanAdapter;
import com.ns.design.pattern.adapter.objectadapter.NormalLoan;

/**
 * @author ns
 * @date 2021/3/26  17:26
 */
public class ObjectAdapterTest {

	public static void main(String[] args) {
		ICBCLoanAdapter icbcLoan = new ICBCLoanAdapter(new NormalLoan());
		icbcLoan.loan();
	}
}
