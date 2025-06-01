package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Category;
import com.example.demo.entity.SkillLog;
import com.example.demo.service.CategoryService;
import com.example.demo.service.SkillLogService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/skilllog-web")
public class SkillLogWebController {
    private final SkillLogService skillLogService;
    private final CategoryService categoryService;

    public SkillLogWebController(SkillLogService skillLogService, CategoryService categoryService) {
        this.skillLogService = skillLogService;
		this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("skillLogs", skillLogService.findAll());
        return "skilllog/list"; // → templates/skilllog/list.html をレンダリング
    }
    
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("skillLog", new SkillLog());
        model.addAttribute("categories", categoryService.findAll());
        return "skilllog/new";
    }
    
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute SkillLog skillLog,
                         BindingResult result,
                         @RequestParam("categoryId") Long categoryId,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "skilllog/new";
        }

        Category category = categoryService.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("カテゴリが見つかりません: ID=" + categoryId));
        skillLog.setCategory(category);

        skillLogService.save(skillLog);
        redirectAttributes.addFlashAttribute("successMessage", "スキルログを登録しました！");
        return "redirect:/skilllog-web";
    }


    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<SkillLog> optionalLog = skillLogService.findById(id);

        if (optionalLog.isEmpty()) {
            throw new IllegalArgumentException("無効なIDです: " + id);
        }

        SkillLog skillLog = optionalLog.get();
        model.addAttribute("skillLog", skillLog);
        model.addAttribute("categories", categoryService.findAll()); // プルダウン用

        return "skilllog/edit"; // テンプレート名（例：edit.html）
    }
    
    @PostMapping("/edit")
    public String update(@ModelAttribute SkillLog skillLog,
                         @RequestParam("categoryId") Long categoryId,
                         RedirectAttributes redirectAttributes) {
        Optional<Category> optionalCategory = categoryService.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new IllegalArgumentException("カテゴリが見つかりません: ID=" + categoryId);
        }
        Category category = optionalCategory.get();
        skillLog.setCategory(category);

        skillLogService.save(skillLog);

        redirectAttributes.addFlashAttribute("successMessage", "スキルログを更新しました！");
        return "redirect:/skilllog-web";
    }

    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        skillLogService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "スキルログを削除しました。");
        return "redirect:/skilllog-web";
    }


}
