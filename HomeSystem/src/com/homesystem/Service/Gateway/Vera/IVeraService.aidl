package com.homesystem.Service.Gateway.Vera;

interface IVeraService {

	void startDataRetrieval(in int id);
	
	void setInterval(in int i);

}