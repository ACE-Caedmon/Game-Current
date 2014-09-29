package com.jcwx.frm.current;
/**
 * Submiter持有者接口,直接get,set就好,使用接口是为了避免多继承问题
 * @author Chenlong
 * */
public interface SubmiterHolder {
	void setSubmiter(ITaskSubmiter submiter);
	ITaskSubmiter getSubmiter();
}
