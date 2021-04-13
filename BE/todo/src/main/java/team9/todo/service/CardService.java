package team9.todo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.todo.domain.Card;
import team9.todo.domain.History;
import team9.todo.domain.User;
import team9.todo.domain.enums.CardColumn;
import team9.todo.domain.enums.HistoryAction;
import team9.todo.exception.NotAuthorizedException;
import team9.todo.exception.NotFoundException;
import team9.todo.repository.CardRepository;
import team9.todo.repository.HistoryRepository;

import java.util.List;

@Service
public class CardService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CardRepository cardRepository;
    private final HistoryRepository historyRepository;

    @Autowired
    public CardService(CardRepository cardRepository, HistoryRepository historyRepository) {
        this.cardRepository = cardRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public Card create(Card card, User user) {
        logger.debug("card 생성 요청: {}, {}, {}", card.getColumnType(), card.getTitle(), card.getContent());
        card.setUser(user.getId());
        Card saved = cardRepository.save(card);

        historyRepository.save(new History(saved.getId(), HistoryAction.ADD, null, saved.getColumnType()));
        return saved;
    }

    public List<Card> getList(CardColumn cardColumn, User user) {
        logger.debug("{}의 카드 목록 요청", cardColumn);
        return cardRepository.findAllByUserAndColumnType(user.getId(), cardColumn.name());
    }

    @Transactional
    public Card update(long cardId, String title, String contents, double priority, User user) {
        logger.debug("{}번 카드의 내용 수정 요청", cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException());
        if (card.getUser() != user.getId()) {
            throw new NotAuthorizedException();
        }
        card.update(title, contents, priority);
        Card saved = cardRepository.save(card);

        historyRepository.save(new History(saved.getId(), HistoryAction.UPDATE, null, null));
        return saved;
    }

    @Transactional
    public Card move(long cardId, CardColumn to, User user) {
        logger.debug("{}번 카드 {}로 이동 요청", cardId, to);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException());
        if (card.getUser() != user.getId()) {
            throw new NotAuthorizedException();
        }
        CardColumn from = card.getColumnType();
        card.setColumnType(to);
        Card saved = cardRepository.save(card);

        historyRepository.save(new History(saved.getId(), HistoryAction.MOVE, from, to));
        return saved;
    }

    @Transactional
    public void delete(long cardId, User user) {
        logger.debug("{}번 카드의 삭제 요청", cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException());
        if (card.getUser() != user.getId()) {
            throw new NotAuthorizedException();
        }
        cardRepository.deleteById(cardId);

        historyRepository.save(new History(card.getId(), HistoryAction.REMOVE, card.getColumnType(), null));
    }
}