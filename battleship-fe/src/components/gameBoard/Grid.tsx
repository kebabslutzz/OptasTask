import React, { useState } from 'react';
import './Grid.css';
import WavesIcon from '@mui/icons-material/Waves';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import BlockIcon from '@mui/icons-material/Block';
import ShotResponse from '../../interfaces/ShotResponse';
import GameState from '../../interfaces/GameState';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import AnchorIcon from '@mui/icons-material/Anchor';
import { Button } from '@mui/material';

const Grid: React.FC = () => {
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

	const handleFetchError = async (response: Response): Promise<boolean> => {
		if (response.status === 401) {
			handleUnauthorized();
			return false;
		}
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}
		return true;
	};

	const startGame = async () => {
		try {
			const response = await fetch('http://localhost:8080/api/v1/battleships/start', {
				method: 'POST',
				credentials: 'include',
			});

			if (await handleFetchError(response)) {
				const result: GameState = await response.json();
				setShotsLeft(result.shotsLeft);
				setGameStarted(true);
				setGameOver(result.gameOver);
				setGameWon(result.gameWon);
				setGridState(
					Array(10)
						.fill(null)
						.map(() => Array(10).fill(null))
				);
			}
		} catch (error) {
			console.error('Error starting game:', error);
		}
	};

	const handleClick = async (row: number, col: number) => {
		if (!gameStarted || gameOver || gridState[row][col] !== null || gameWon) {
			return;
		}

		const shootRequest = {
			x: col,
			y: row,
		};

		try {
			const response = await fetch('http://localhost:8080/api/v1/battleships/shoot', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				credentials: 'include',
				body: JSON.stringify(shootRequest),
			});

			if (await handleFetchError(response)) {
				const result: ShotResponse = await response.json();
				const newGridState = [...gridState];
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
			}
		} catch (error) {
			console.error('Error:', error);
		}
	};

	const renderGrid = () => {
		const grid = [];
		for (let row = 0; row < 10; row++) {
			for (let col = 0; col < 10; col++) {
				const cellState = gridState[row][col];
				grid.push(
					<button
						key={`${row}-${col}`}
						className={`grid-button ${cellState === null ? '' : cellState ? 'hit' : 'miss'}`}
						onClick={() => handleClick(row, col)}
						disabled={!gameStarted || gameOver || cellState !== null}
					>
						{cellState === null ? (
							<WavesIcon className='wave' />
						) : cellState === true ? (
							<DirectionsBoatIcon className='ship' />
						) : cellState === 'destroyed' ? (
							<AnchorIcon className='destroyed' />
						) : (
							<BlockIcon className='miss' />
						)}
					</button>
				);
			}
		}
		return grid;
	};

	return (
		<div className='game-container'>
			<div className='game-info'>
				<Button onClick={startGame} variant='contained'>
					{gameStarted ? 'Restart Game' : 'Start Game'}
				</Button>
				<h3>
					Shots Left: <span style={{ color: shotsLeft >= shots * 0.2 ? 'inherit' : 'red' }}>{shotsLeft}</span>
				</h3>
				{gameWon && <p className='game-state-container game-won'>Game Won!</p>}
				{gameOver && !gameWon && <p className='game-state-container game-over'>Game Over!</p>}
			</div>
			<div className='grid-container'>{renderGrid()}</div>
		</div>
	);
};

export default Grid;
