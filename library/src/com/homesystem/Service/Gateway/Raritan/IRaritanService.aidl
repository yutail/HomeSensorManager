package com.homesystem.Service.Gateway.Raritan;

import com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback;

interface IRaritanService {

	void startDataRetrieval(in int id);
	
	void stopDataRetrieval(in int id);
	
	void setInterval(in int i);
	
	void setChannel(in String channel);
	
	void registerRaritanCallback(IRaritanServiceCallback raritan_cb);
	
	void unregisterRaritanCallback(IRaritanServiceCallback raritan_cb);

}