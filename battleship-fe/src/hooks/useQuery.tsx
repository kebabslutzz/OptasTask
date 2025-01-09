import { useState } from 'react';
import GameState from '../interfaces/GameState';
import ShotResponse from '../interfaces/ShotResponse';

const useQuery = () => {
	const [error, setError] = useState<string | null>(null);

	const handleFetchError = async (response: Response): Promise<boolean> => {
		if (response.status === 401) {
			setError('Unauthorized');
			return false;
		}
		if (!response.ok) {
			setError(`HTTP error! status: ${response.status}`);
			return false;
		}
		return true;
	};

	const startGame = async (): Promise<GameState | null> => {
		try {
			const response = await fetch('http://localhost:8080/api/v1/battleships/start', {
				method: 'POST',
				credentials: 'include',
			});

			if (await handleFetchError(response)) {
				const result: GameState = await response.json();
				return result;
			}
		} catch (error) {
			setError('Error starting game');
			console.error('Error starting game:', error);
		}
		return null;
	};

	const shoot = async (x: number, y: number): Promise<ShotResponse | null> => {
		const shootRequest = { x, y };

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
				return result;
			}
		} catch (error) {
			setError('Error shooting');
			console.error('Error shooting:', error);
		}
		return null;
	};

	return { startGame, shoot, error };
};

export default useQuery;
