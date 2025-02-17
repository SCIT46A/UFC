package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("")
    public String creatorDashboard() {
        return "dashboard/creator-dashboard";
    }

    @GetMapping("/products/register")
    public String getProdcutRegisterPage() {
        return "dashboard/product-register :: product-register";
    }

    @GetMapping("/products/management")
    public String getProdcutManagementPage() {
        return "dashboard/product-management :: product-management";
    }

    @GetMapping("/products/orders")
    public String getOrderManagementPage() {
        return "dashboard/product-orders :: product-orders";
    }

    @GetMapping("/settlements")
    public String getSettlementManagementPage() {
        return "dashboard/settlements :: settlements";
    }

    @GetMapping("/campaigns/management")
    public String getCampaignManagementPage() {
        return "dashboard/campaign-management :: campaign-management";
    }

    @GetMapping("/campaigns/donation/management")
    public String getDonationManagementPage() {
        return "dashboard/donation-management :: donation-management";
    }

    @GetMapping("/delivery")
    public String getDeliveryPage() {
        return "dashboard/delivery :: delivery";
    }

    @GetMapping("/reviews")
    public String getReviewsPage() {
        return "dashboard/reviews :: reviews";
    }

    @GetMapping("/inquiries")
    public String getInquiriesPage() {
        return "dashboard/inquiries :: inquiries";
    }
}
