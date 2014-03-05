package com.homesystem.Service.Gateway.Veris;

import com.homesystem.Service.Gateway.Veris.IVerisServiceCallback;

interface IVerisService {

	void startDataRetrieval();
	
	void setInterval(in int i);
	
	void registerVerisCallback(IVerisServiceCallback veris_cb);
	
	void unregisterVerisCallback(IVerisServiceCallback veris_cb);
}