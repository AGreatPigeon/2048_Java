package ai;

import eval.Evaluator;
import model.AbstractState.MOVE;
import model.State;

public class PaulBurnsEval extends AbstractPlayer {

	PaulBurnsEvaluator eval;

	@Override
	public MOVE getMove(State game) {
		
		int depth = 4;
		
		pause();

		eval = new PaulBurnsEvaluator(game);

		double bestScore = Double.NEGATIVE_INFINITY;

		MOVE bestMove = game.getMoves().get(0);

		for (MOVE move : game.getMoves()) {
			game.move(move);
			double score = 0;

			for (int i = 0; i < 100; ++i) {
				score += DFS(game, depth);
			}

			score /= 100;

			if (score >= bestScore) {
				bestScore = score;
				bestMove = move;
			}
			game.undo();
		}

		return bestMove;

	}

	private synchronized double DFS(State game, int depth) {

		double bestScore = Double.MIN_VALUE;

		if (depth == 0) {
			bestScore = eval.evaluate(game);
		} else {
			for (MOVE move : game.getMoves()) {
				game.move(move);
				bestScore = Math.max(bestScore, DFS(game, depth - 1));
				game.undo();
			}
		}
		return bestScore;
	}

	@Override
	public int studentID() {
		return 201209753;
	}

	@Override
	public String studentName() {
		return "Paul Burns";
	}
	
	private class PaulBurnsEvaluator implements Evaluator {

		State game;

		double smoothWeight = 5;
		double monoWeight = 1.3;

		public PaulBurnsEvaluator(State game) {
			this.game = game;
		}

		@Override
		public double evaluate(State state) {
			
			double eval = emptyTiles() * emptyTiles() * snake()
					+ calculateClusteringScore() * smoothWeight + monotonicity() + monoWeight;
			return eval;
		}

		/**
		 * Calculates a heuristic variance-like score that measures how
		 * clustered the board is.
		 * 
		 * @param boardArray
		 * @return
		 */
		private int calculateClusteringScore() {
			int clusteringScore = 0;

			int[] neighbours = { -1, 0, 1 };

			for (int x = 0; x < 4; ++x) {
				for (int y = 0; y < 4; ++y) {
					if (game.getValue(x, y) == 0) {
						continue; // ignore empty cells
					}

					// for every pixel find the distance from each neighbours
					int numOfNeighbors = 0;
					int sum = 0;
					for (int k : neighbours) {
						int a = x + k;
						if (a < 0 || a >= 4) {
							continue;
						}
						for (int l : neighbours) {
							int b = y + l;
							if (b < 0 || b >= 4) {
								continue;
							}

							if (game.getValue(x, y) > 0) {
								++numOfNeighbors;
								sum += Math.abs(game.getValue(x, y)
										- game.getValue(x, y));
							}

						}
					}

					clusteringScore += sum / numOfNeighbors;
				}
			}

			return clusteringScore;
		}

		public int emptyTiles() {
			int empty = 0;
			for (int x = 0; x < 4; ++x) {
				for (int y = 0; y < 4; ++y) {
					if (game.getValue(x, y) == 0) {
						++empty;
					}
				}
			}
			return empty;
		}

		public double monotonicity() {
			int[] totals = { 0, 0, 0, 0 };

			// up/down direction
			for (int x = 0; x < 4; ++x) {
				int current = 0;
				int next = current + 1;
				double currentValue;
				double nextValue;
				while (next < 4) {
					while (next < 4 && (game.getValue(x, next) != 0)) {
						next++;
					}

					if (next >= 4) {
						next--;
					}

					if (game.getValue(x, current) != 0) {
						currentValue = Math.log(game.getValue(x, current)
								/ Math.log(2));
					} else {
						currentValue = 0;
					}

					if (game.getValue(x, next) != 0) {
						nextValue = Math.log(game.getValue(x, next)
								/ Math.log(2));
					} else {
						nextValue = 0;
					}

					if (currentValue > nextValue) {
						totals[0] += nextValue - currentValue;
					} else if (nextValue > currentValue) {
						totals[1] += currentValue - nextValue;
					}
					current = next;
					next++;
				}
			}

			// left/right direction
			for (int y = 0; y < 4; y++) {
				int current = 0;
				int next = current + 1;
				double currentValue;
				double nextValue;
				while (next < 4) {
					while (next < 4 && game.getValue(next, y) != 0) {
						next++;
					}
					if (next >= 4) {
						next--;
					}
					if (game.getValue(current, y) != 0) {
						currentValue = Math.log(game.getValue(current, y)
								/ Math.log(2));
					} else {
						currentValue = 0;
					}

					if (game.getValue(next, y) != 0) {
						nextValue = Math.log(game.getValue(next, y)
								/ Math.log(2));
					} else {
						nextValue = 0;
					}

					if (currentValue > nextValue) {
						totals[2] += nextValue - currentValue;
					} else if (nextValue > currentValue) {
						totals[3] += currentValue - nextValue;
					}
					current = next;
					next++;
				}
			}

			return Math.max(totals[0], totals[1])
					+ Math.max(totals[2], totals[3]);
		}

		private double snake() {
			double[][] weightedGrid = { { 10, 8, 7, 6.5 }, { .5, .7, 1, 3 },
									  { -.5, -1.5, -1.8, -2 }, { -3.8, -3.7, -3.5, -3 } };

			double value = 0;

			for (int x = 0; x < 4; ++x) {
				for (int y = 0; y < 4; ++y) {
					value += weightedGrid[x][y] * game.getValue(x, y);
				}
			}

			return value;
		}
	}
}
