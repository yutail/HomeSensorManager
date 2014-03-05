package com.homesystem.Service.Gateway.Vera;

import com.homesystem.Service.Gateway.Vera.IVeraServiceCallback;

interface IVeraService {

	void startDataRetrieval(in int id);
	
	void stopDataRetrieval(in int id);
	
	void setInterval(in int i);
	
	void registerVeraCallback(IVeraServiceCallback vera_cb);
	
	void unregisterVeraCallback(IVeraServiceCallback vera_cb);
}