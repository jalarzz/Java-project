package de.tum.cit.ase.maze;

/**
 * Enumeration of possible states for an enemy.
 */
public enum EnemyState {
    PATROLLING, // Enemy is moving around in a predefined pattern and area.
    CHASING,    // Enemy is pursuing the player character.
    FLEEING  ,   // Enemy is moving away from the player character.
    STOPPED
}