package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/password")
    public String passwordForm(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "user/password";
    }

    @PostMapping("/user/password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto dto,
            BindingResult bindingResult,
            RedirectAttributes ra,
            Model model){

        if(bindingResult.hasErrors()){
            return "user/password";
        }

        if(!dto.getNewPassword().equals(dto.getConfirmNewPassword())){
            bindingResult.rejectValue("confirmNewPassword","mismatch","새 비밀번호가 일치하지 않습니다");
            return "user/password";
        }

        try{
            userService.changePassword(userDetails.getUsername(), dto.getCurrentPassword(), dto.getNewPassword());
            ra.addFlashAttribute("successMessage", "비밀번호 변경 성공");
        } catch(IllegalArgumentException e){
            bindingResult.rejectValue("currentPassword", "wrong", e.getMessage());
            return "user/password";
        }

        return "redirect:/home";
    }
}
