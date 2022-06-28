package com.academy.car_rental.controller;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Comment;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.service.AccidentService;
import com.academy.car_rental.service.CommentService;
import com.academy.car_rental.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

import static com.academy.car_rental.constant.Constants.*;
import static com.academy.car_rental.constant.Constants.MESSAGE;

@Controller
@RequestMapping("/accidents")
@RequiredArgsConstructor
public class AccidentController {
    private final AccidentService accidentService;
    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/showAccidentByOrder/{id}")
    public String showAccidentByOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            var accident = accidentService.getById(id);
            var order = orderService.getById(accident.getOrder().getId());
            List<Accident> accidents = accidentService.getAllAccidentsByOrder(order);
            BigDecimal totalCost = BigDecimal.valueOf(0);
            for (Accident entry : accidents) {
                totalCost = totalCost.add(entry.getCost());

            }

            model.addAttribute(TOTAL_COST_FOR_MODEL, totalCost);
            model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
            model.addAttribute(ORDER_FOR_MODEL, order);
        } catch (ServiceException e) {
            model.addAttribute(MESSAGE, "There are no incidents for this order");
            return "redirect:/orders";
        }

        return "accident/accident";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/createNew/{id}")
    public String createNewAccident(@PathVariable("id") Integer id,
                                    RedirectAttributes ra, Model model) {
        try {
            var order = orderService.getById(id);
            if(orderService.isOrderActive(order)) {
                model.addAttribute(ACCIDENT_FOR_MODEL, new Accident());
                model.addAttribute(ORDER_FOR_MODEL, order);
                model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new accident");
            } else{
                ra.addFlashAttribute(MESSAGE, "This order isn't active. You can't change something");
                return "redirect:/orders";
            }
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The order wasn't found." + e.getMessage());
            return "redirect:accident/accidents";
        }
        return "accident/accident_form";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping()
    public String showAccidentList(Model model) {

        List<Accident> accidents = accidentService.getAll();
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);

        return "accident/accidents";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/new")
    public String showNewForm(Model model) {
        model.addAttribute(ACCIDENT_FOR_MODEL, new Accident());
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new accident");

        return "accident/accident_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/save")
    public String saveAccident(@Valid Accident accident,
                               @RequestParam("cost") BigDecimal cost,
                               @RequestParam("description") String description,
                               Integer orderId,
                               RedirectAttributes ra) {
        if (accidentService.createNewAccident(accident, cost, description, orderId)) {
            ra.addFlashAttribute(MESSAGE, "The accident has been added successfully.");
        } else {
            ra.addFlashAttribute(MESSAGE, "The accident hasn't been added. Something go wrong ");
        }
        return "redirect:/orders";
    }


    @GetMapping(value = "/delete/{id}")
    public String deleteAccident(@PathVariable("id") Integer id, RedirectAttributes ra) {
        accidentService.delete(id);
        return "redirect:accident/accidents";
    }

    @GetMapping(value = "/edit/{id}")
    public String showEditAccidentForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Accident accident = accidentService.getById(id);
            model.addAttribute(ACCIDENT_FOR_MODEL, accident);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit accident (id: " + id + ")");
            return "accident/accident_form";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The accident wasn't found." + e.getMessage());
            return "redirect:accident/accidents";
        }
    }
}
