package ua.reed.quizservice.service.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class GameHelperServiceImpl implements GameHelperService {

    private final AtomicBoolean isGameOver = new AtomicBoolean();

    @Override
    public void restartGame() {
        isGameOver.set(false);
    }

    @Override
    public boolean isGameOngoing() {
        return !isGameOver.get();
    }

    @Override
    public void stopGame() {
        isGameOver.set(true);
    }
}
