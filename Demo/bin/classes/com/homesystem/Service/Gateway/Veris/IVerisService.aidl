package com.homesystem.Service.Gateway.Veris;

import com.homesystem.Service.Gateway.Veris.IVerisServiceCallback;

interface IVerisService {

	void startDataRetrieval();
	
	void stopDataRetrieval();
	
	void setInterval(in int i);
	
	void setRegAddr(in int reg_addr);
	
	void setRegQty(in int reg_qty);
	
	void registerVerisCallback(IVerisServiceCallback veris_cb);
	
	void unregisterVerisCallback(IVerisServiceCallback veris_cb);
}