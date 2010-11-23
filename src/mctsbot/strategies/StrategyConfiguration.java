package mctsbot.strategies;

import mctsbot.strategies.actionselection.*;
import mctsbot.strategies.backpropagation.*;
import mctsbot.strategies.selection.*;
import mctsbot.strategies.simulation.*;

public class StrategyConfiguration {
	
	private final ActionSelectionStrategy actionSelectionStrategy;
	private final SelectionStrategy selectionStrategy;
	private final SimulationStrategy simulationStrategy;
	private final BackpropagationStrategy backpropagationStrategy;
	
	public StrategyConfiguration(
			ActionSelectionStrategy actionSelectionStrategy, 
			SelectionStrategy selectionStrategy, 
			SimulationStrategy simulationStrategy, 
			BackpropagationStrategy backpropagationStrategy) {
		
		this.actionSelectionStrategy = actionSelectionStrategy;
		this.selectionStrategy = selectionStrategy;
		this.simulationStrategy = simulationStrategy;
		this.backpropagationStrategy = backpropagationStrategy;
	}

	public ActionSelectionStrategy getActionSelectionStrategy() {
		return actionSelectionStrategy;
	}

	public SelectionStrategy getSelectionStrategy() {
		return selectionStrategy;
	}

	public SimulationStrategy getSimulationStrategy() {
		return simulationStrategy;
	}

	public BackpropagationStrategy getBackpropagationStrategy() {
		return backpropagationStrategy;
	}
	
	public static StrategyConfiguration getDefault() {
		return new StrategyConfiguration(
				new HighestEVActionSelectionStrategy(), 
				new UCTSelectionStrategy(), 
				new AveragingSimulationStrategy(new StaticDistributionSimulationStrategy(), 10), 
				new AveragingBackpropagationStrategy() );
	}
	
}
