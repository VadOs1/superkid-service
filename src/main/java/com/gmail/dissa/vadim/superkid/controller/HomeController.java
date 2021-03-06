package com.gmail.dissa.vadim.superkid.controller;

import com.gmail.dissa.vadim.superkid.domain.Order;
import com.gmail.dissa.vadim.superkid.exception.SuperkidException;
import com.gmail.dissa.vadim.superkid.service.CRMService;
import com.gmail.dissa.vadim.superkid.service.ProductService;
import com.gmail.dissa.vadim.superkid.service.SendMailService;
import com.gmail.dissa.vadim.superkid.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
@RequestMapping("/")
public class HomeController {
    private final ProductService productService;
    private final CRMService crmService;
    private final ShoppingCartService shoppingCartService;
    private final SendMailService sendMailService;
    private final ExecutorService executorService;

    @Autowired
    public HomeController(ProductService productService,
                          CRMService crmService,
                          ShoppingCartService shoppingCartService,
                          SendMailService sendMailService) {
        this.productService = productService;
        this.crmService = crmService;
        this.shoppingCartService = shoppingCartService;
        this.sendMailService = sendMailService;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    @PreDestroy
    public void preDestroy() {
        executorService.shutdown();
    }

    @GetMapping("/")
    public ModelAndView home(ModelAndView modelAndView) {
        modelAndView.addObject("products", productService.getProducts());
        modelAndView.addObject("productsInCart", shoppingCartService.getProducts());
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/products/{article}")
    public ModelAndView product(@PathVariable("article") String article,
                                ModelAndView modelAndView) {
        modelAndView.addObject("productsByArticle", productService.getProductsByArticle(article));
        modelAndView.addObject("productsInCart", shoppingCartService.getProducts());
        modelAndView.addObject("featuredProducts", productService.getProducts());
        modelAndView.setViewName("product");
        return modelAndView;
    }

    @GetMapping("/cart/")
    public ModelAndView cart(ModelAndView modelAndView) {
        modelAndView.addObject("productsInCart", shoppingCartService.getProducts());
        modelAndView.addObject("productsInCartAmount", shoppingCartService.getAmountOfGoodsInCart());
        modelAndView.setViewName("cart");
        return modelAndView;
    }

    @GetMapping("/cart/new/")
    public ModelAndView cartNew(@RequestParam(value = "article") String article,
                                @RequestParam(value = "size") long size,
                                ModelAndView modelAndView) {
        modelAndView.addObject("product", shoppingCartService.addProduct(productService.findByProductInfoArticleAndProductSizeId(article, size)));
        modelAndView.setViewName("forward:/cart/");
        return modelAndView;
    }

    @GetMapping("/cart/clear/")
    public ModelAndView cartClear(ModelAndView modelAndView) {
        modelAndView.addObject("products", shoppingCartService.removeProducts());
        modelAndView.setViewName("forward:/cart/");
        return modelAndView;
    }

    @PostMapping("/checkout/")
    public ModelAndView checkout(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "email") String email,
                                 @RequestParam(value = "phone") String phone,
                                 ModelAndView modelAndView) {
        Order order = crmService.saveOrder(name, email, phone);
        executorService.execute(() -> sendMailService.sendMail(order));
        modelAndView.addObject("order", order);
        modelAndView.addObject("productsInCart", new ArrayList<>(shoppingCartService.getProducts()));
        modelAndView.addObject("productsInCartAmount", shoppingCartService.getAmountOfGoodsInCart());
        modelAndView.setViewName("checkout");
        shoppingCartService.removeProducts();
        return modelAndView;
    }

    @GetMapping("/checkout/")
    public void checkoutGet() {
        throw new SuperkidException("GET method in checkout is not supported");
    }

    @GetMapping("/login/")
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_login");
        return modelAndView;
    }
}