package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Game;
import team.bham.repository.GameRepository;
import team.bham.service.dto.GameDTO;
import team.bham.service.mapper.GameMapper;

/**
 * Service Implementation for managing {@link Game}.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    /**
     * Save a game.
     *
     * @param gameDTO the entity to save.
     * @return the persisted entity.
     */
    public GameDTO save(GameDTO gameDTO) {
        log.debug("Request to save Game : {}", gameDTO);
        Game game = gameMapper.toEntity(gameDTO);
        game = gameRepository.save(game);
        return gameMapper.toDto(game);
    }

    /**
     * Update a game.
     *
     * @param gameDTO the entity to save.
     * @return the persisted entity.
     */
    public GameDTO update(GameDTO gameDTO) {
        log.debug("Request to update Game : {}", gameDTO);
        Game game = gameMapper.toEntity(gameDTO);
        game = gameRepository.save(game);
        return gameMapper.toDto(game);
    }

    /**
     * Partially update a game.
     *
     * @param gameDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GameDTO> partialUpdate(GameDTO gameDTO) {
        log.debug("Request to partially update Game : {}", gameDTO);

        return gameRepository
            .findById(gameDTO.getId())
            .map(existingGame -> {
                gameMapper.partialUpdate(existingGame, gameDTO);

                return existingGame;
            })
            .map(gameRepository::save)
            .map(gameMapper::toDto);
    }

    /**
     * Get all the games.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameDTO> findAll() {
        log.debug("Request to get all Games");
        return gameRepository.findAll().stream().map(gameMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one game by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GameDTO> findOne(Long id) {
        log.debug("Request to get Game : {}", id);
        return gameRepository.findById(id).map(gameMapper::toDto);
    }

    /**
     * Get all games with matchId.
     *
     * @param id the id of the match.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameDTO> findAllGamesByMatchId(Long id) {
        log.debug("Request to get Games By MatchId : {}", id);
        return gameRepository.findAllByMatchId(id).stream().map(gameMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Delete the game by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Game : {}", id);
        gameRepository.deleteById(id);
    }
}
