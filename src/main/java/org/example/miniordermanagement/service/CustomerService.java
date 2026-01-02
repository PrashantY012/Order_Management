package org.example.miniordermanagement.service;
import jakarta.transaction.Transactional;
import org.example.miniordermanagement.dto.CustomerDto;
import org.example.miniordermanagement.models.Customer;
import org.springframework.stereotype.Service;
import org.example.miniordermanagement.repository.CustomerRepo;


@Service
public class CustomerService {
    private final CustomerRepo customerRepo;
    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    private Customer getCustomerFromDto(CustomerDto customerDto) {
        return new Customer(customerDto.getName(), customerDto.getEmail());
    }

    private CustomerDto getCustomerDtoFromCustomer(Customer customer) {
        if(customer == null) return null;//TODO: throw not found exception
        return new CustomerDto(customer.getName(), customer.getEmail());
    }


    public String registerCustomer(CustomerDto customerDto){
        Customer customer = getCustomerFromDto(customerDto); //TODO: error, if already there
        customerRepo.save(customer);
        return "Success"; //TODO: better response
    }

    public CustomerDto getCustomer(String email){ //TODO: error handling
        Customer customer = customerRepo.findByEmail(email);
        return getCustomerDtoFromCustomer(customer);
    }

//    public List<OrderDto> getAllOrderForCustomer(Customer customer){
//        List<SpringDataJaxb.OrderDto> orderDtos = new ArrayList<>();
//    }

    @Transactional //needed for deletion
    public String deleteCustomer(String email){
        Long entryDeleted = customerRepo.deleteByEmail(email);
        return  (entryDeleted ==1 ? "Successfully deleted "+email:"Customer doesnt exist with email "+email);
    }



}
