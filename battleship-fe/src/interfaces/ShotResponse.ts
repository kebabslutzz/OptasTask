interface ShotResponse {
	hit: boolean;
	shotsLeft: number;
	gameOver: boolean;
	gameWon: boolean;
	destroyedShipCoordinates: number[][];
}

export default ShotResponse;
