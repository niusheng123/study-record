package com.ns.design.pattern.responsibility;

/**
 * @author ns
 * @date 2021/4/7  9:25
 */
public abstract class AbstractHandler<T> {
	
	protected AbstractHandler chain;
	
	public void next(AbstractHandler handler) {
		this.chain = handler;
	}
	
	public abstract void doHandler(User user);
	
	public static class Builder<T> {
		
		private AbstractHandler<T> head;
		
		private AbstractHandler<T> tail;
		
		public Builder<T> addHandler(AbstractHandler<T> handler) {
			if (this.head == null) {
				this.head = this.tail = handler;
				return this;
			}
			this.tail.next(handler);
			this.tail = handler;
			return this;
		}
		
		public AbstractHandler<T> builder() {
			return this.head;
		}
	}
}
