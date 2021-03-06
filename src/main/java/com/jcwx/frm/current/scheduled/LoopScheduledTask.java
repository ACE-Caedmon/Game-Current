package com.jcwx.frm.current.scheduled;
/**
 * @author Chenlong
 * 定时循环执行线程封装类
 * */
public abstract class LoopScheduledTask extends ScheduledTask{
	protected boolean fixRate;//
	protected long period;//定时执行时间间隔
	public LoopScheduledTask(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
    /**
     * @return  是否绝对固定频率执行
     * */
	public boolean isFixRate() {
		return fixRate;
	}
    /**
     * @param fixRate  是否绝对固定频率执行
     * */
	public void setFixRate(boolean fixRate) {
		this.fixRate = fixRate;
	}
    /**
     * @return 循环执行任务间隔
     * */
	public long getPeriod() {
		return period;
	}
    /**
     * @param period 循环执行任务间隔
     * */
	public void setPeriod(long period) {
		this.period = period;
	}
}
