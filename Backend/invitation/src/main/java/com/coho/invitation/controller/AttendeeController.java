package com.coho.invitation.controller;

import com.coho.invitation.security.UserAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="5) Attendee API",description = "참석자 API")
@UserAuthorize
@RestController
@RequestMapping("/api/attendees")
@CrossOrigin(origins = "*")
public class AttendeeController {
    /* 행사 참석자 조회 (전체, 참석자 이름, 초대자) */

    /* 초대자가 참석자 직접 추가 */

    /* 참석자가 버튼으로 추가 */

    /* 참석자 정보 수정 */

    /* 부조(부조 금액, 방법, 비고) 정보 수정 */

    /* 참석자 삭제 - 불참시 */

}
