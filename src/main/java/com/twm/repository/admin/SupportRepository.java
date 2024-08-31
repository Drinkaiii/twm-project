package com.twm.repository.admin;

import com.twm.dto.ReturnSupportDto;

import java.util.List;

public interface SupportRepository {

    List<ReturnSupportDto> getSupportAll();

}
