package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> pageProducts;

        if(normalizedKeyword != null) {
            pageProducts = productService.searchProducts(normalizedKeyword, pageRequest);
        } else {
            pageProducts = productService.getProducts(pageRequest);
        }

        model.addAttribute("pageProducts", pageProducts);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());

        model.addAttribute("productDto", productDto);
        model.addAttribute("productId", id);
        return "products/edit";
    }

    @PostMapping("/{id}/edit")
    public String editProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra){

        if(bindingResult.hasErrors()) {
            model.addAttribute("productId",id);
            return "products/edit";
        }

        productService.updateProduct(id, productDto);
        ra.addFlashAttribute("successMessage", "성공적으로 수정하였습니다");
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
