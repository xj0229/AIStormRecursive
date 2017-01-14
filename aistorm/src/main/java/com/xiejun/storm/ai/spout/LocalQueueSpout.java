package com.xiejun.storm.ai.spout;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.trident.spout.ITridentSpout;
import org.apache.storm.tuple.Fields;

public class LocalQueueSpout<T> implements ITridentSpout<Long>{
    private static final long serialVersionUID = 1L;
    SpoutOutputCollector collector;
    BatchCoordinator<Long> coordinator = new DefaultCoordinator();
    Emitter<Long> emitter;

    public LocalQueueSpout(Emitter<Long> emitter) {
        this.emitter = emitter;
    }

	@Override
	public BatchCoordinator<Long> getCoordinator(String txStateId, Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Emitter<Long> getEmitter(String txStateId, Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fields getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}
}
