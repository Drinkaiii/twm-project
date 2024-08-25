package com.twm.repository.chat;

import com.twm.dto.ButtonDto;

import java.util.List;

public interface ChatRepository {

    List<ButtonDto> findAllButtons();

}
