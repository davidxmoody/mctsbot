package mctsbot.strategies;

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
	
}
