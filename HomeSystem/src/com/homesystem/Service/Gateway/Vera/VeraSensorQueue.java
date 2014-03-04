package com.homesystem.Service.Gateway.Vera;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;

public class VeraSensorQueue {
	
	private static final int SENSORQUEUESIZE = 10;
	public static BlockingQueue<MotherSensor> mSensorQueue = 
			new ArrayBlockingQueue<MotherSensor>(SENSORQUEUESIZE);	
	
	public static BlockingQueue<Integer> mSensorSize = 
			new ArrayBlockingQueue<Integer>(1);

}
