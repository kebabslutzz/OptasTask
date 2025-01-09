import React, { useState } from 'react';
import './Game.css';
import { Button, Container } from '@mui/material';
import useQuery from '../../hooks/useQuery';
import renderGrid from './renderGrid/renderGrid';

const Game: React.FC = () => {
	const shots = 25;
	const [shotsLeft, setShotsLeft] = useState(shots);
	const [gameOver, setGameOver] = useState(false);
	const [gameWon, setGameWon] = useState(false);
	const [gameStarted, setGameStarted] = useState(false);
	const [gridState, setGridState] = useState<(boolean | null | 'destroyed')[][]>(
		Array(10)
			.fill(null)
			.map(() => Array(10).fill(null))
	);
	const { startGame, shoot, error } = useQuery();

	const handleUnauthorized = () => {
		setGameStarted(false);
		setGameOver(false);
		setGameWon(false);
		setShotsLeft(25);
		setGridState(
			Array(10)
				.fill(null)
				.map(() => Array(10).fill(null))
		);
	};

	const handleStartGame = async () => {
		const result = await startGame();
		if (result) {
			setShotsLeft(result.shotsLeft);
			setGameStarted(true);
			setGameOver(result.gameOver);
			setGameWon(result.gameWon);
			setGridState(
				Array(10)
					.fill(null)
					.map(() => Array(10).fill(null))
			);
		} else if (error === 'Unauthorized') {
			handleUnauthorized();
		}
	};

	const handleClick = async (row: number, col: number) => {
		if (!gameStarted || gameOver || gridState[row][col] !== null || gameWon) {
			return;
		}

		const result = await shoot(col, row);
		if (result) {
			const newGridState = gridState.map((row) => row.slice()); // Create a deep copy of the gridState
			newGridState[row][col] = result.hit;

			if (result.destroyedShipCoordinates) {
				result.destroyedShipCoordinates.forEach(([x, y]) => {
					if (newGridState[y] && newGridState[y][x] !== undefined) {
						newGridState[y][x] = 'destroyed';
					}
				});
			}

			setGridState(newGridState);
			setShotsLeft(result.shotsLeft);
			setGameWon(result.gameWon);
			setGameOver(result.gameOver);
		} else if (error === 'Unauthorized') {
			handleUnauthorized();
		}
	};

	return (
		<Container className='game-container'>
			<div className='game-info'>
				<Button
					onClick={handleStartGame}
					variant='contained'
					className={`start-game-button ${gameStarted && !gameOver ? 'disabled-start-game-button' : ''}`}
					disabled={gameStarted && !gameOver}
				>
					{gameStarted ? 'Restart Game' : 'Start Game'}
				</Button>
				<h3>
					{gameStarted ? (
						<>
							Shots Left: <span style={{ color: shotsLeft >= shots * 0.2 + 1 ? 'inherit' : 'red' }}>{shotsLeft}</span>
						</>
					) : (
						'Start the game to play'
					)}
				</h3>
				{gameWon && <p className='game-state-container game-won'>Game Won!</p>}
				{gameOver && !gameWon && <p className='game-state-container game-over'>Game Over!</p>}
			</div>
			<div className='grid-container'>{renderGrid(gridState, handleClick, gameStarted, gameOver)}</div>
		</Container>
	);
};

export default Game;
