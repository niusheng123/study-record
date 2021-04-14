package com.ns.design.pattern.adapter.objectadapter;


/**
 * @author ns
 * @date 2021/3/26  14:02
 */
public class ICBCLoanAdapter implements ILoan {
	
	private NormalLoan normalLoan;

	public ICBCLoanAdapter(NormalLoan normalLoan) {
		this.normalLoan = normalLoan;
	}

	@Override
	public int loan() {
		int maxLoanMoney = normalLoan.loan();
		System.out.println("根据风控计算，最多借出30%申请金额，可借额度：" + maxLoanMoney * 0.3);
		return (int) (maxLoanMoney * 0.3);
	}
}
