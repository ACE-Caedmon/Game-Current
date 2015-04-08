package com.jcwx.frm.current;
/**
 * Actor持有者接口,直接get,set就好,使用接口是为了避免多继承问题
 * @author Chenlong
 * */
public interface ActorHolder {
	void setActor(IActor actor);
	IActor getActor();
}
