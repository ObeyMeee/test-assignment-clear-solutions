package ua.com.andromeda.testassignment.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.com.andromeda.testassignment.dto.Dto;
import ua.com.andromeda.testassignment.exception.InvalidRangeException;
import ua.com.andromeda.testassignment.exception.InvalidUUIDException;
import ua.com.andromeda.testassignment.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;
    private final String baseUrl = "/users";

    User getDefaultUser() {
        User user = new User();
        user.setFirstName("Andrii");
        user.setLastName("Heraskin");
        user.setEmail("andromeda@gmail.com");
        user.setAddress("Peremohy Street 20");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setPhoneNumber("+380678955568");
        return user;
    }

    @Test
    void findById_shouldReturnUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = getDefaultUser();
        user.setId(id);
        when(userService.findById(any())).thenReturn(user);

        mockMvc.perform(get(baseUrl + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new Dto<>(user))));
    }

    @Test
    void findById_shouldReturnBadRequest_invalidUUID() throws Exception {
        String invalidId = "invalidUUID";
        InvalidUUIDException exception = new InvalidUUIDException();
        when(userService.findById(invalidId)).thenThrow(exception);

        mockMvc.perform(get(baseUrl + "/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(exception.getMessage()));
    }

    @Test
    void findById_shouldReturnNotFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        when(userService.findById(notFoundId.toString())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get(baseUrl + "/" + notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByBirthDateBetween_shouldThrowInvalidRangeException() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        LocalDate from = LocalDate.parse("2000-01-01");
        LocalDate to = LocalDate.parse("1990-01-01");
        params.add("from", from.toString());
        params.add("to", to.toString());
        String errMessage = "'From' date must be less than 'to'";
        when(userService.findAllByBirthDateBetween(eq(from), eq(to), any(Pageable.class)))
                .thenThrow(new InvalidRangeException(errMessage));

        mockMvc.perform(get(baseUrl + "/search/birthDate/between")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errMessage));

        verify(userService, times(1))
                .findAllByBirthDateBetween(eq(from), eq(to), any(Pageable.class));
    }

    @Test
    void findAllByBirthDateBetween_shouldReturnDtoPage() throws Exception {
        MultiValueMap<String, String> requestParams = getParams();
        LocalDate from = LocalDate.parse(requestParams.getFirst("from"));
        LocalDate to = LocalDate.parse(requestParams.getFirst("to"));
        List<User> users = List.of(new User(), new User(), new User(), new User());
        Pageable pageable = PageRequest.of(
                parseInt(requestParams.getFirst("page")),
                parseInt(requestParams.getFirst("size"))
        );
        Page<User> expectedPage = new PageImpl<>(users, pageable, users.size());

        when(userService.findAllByBirthDateBetween(from, to, pageable))
                .thenReturn(expectedPage);

        mockMvc.perform(get(baseUrl + "/search/birthDate/between")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new Dto<>(expectedPage))));

        verify(userService, times(1))
                .findAllByBirthDateBetween(from, to, pageable);
    }

    private MultiValueMap<String, String> getParams() {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "1990-10-02");
        requestParams.add("to", "2000-11-03");
        requestParams.add("page", "0");
        requestParams.add("size", "10");
        return requestParams;
    }

    @Test
    void save_shouldReturnDtoUserWithId() throws Exception {
        User userToSave = getDefaultUser();
        UUID id = UUID.randomUUID();
        userToSave.setId(id);
        when(userService.save(any(User.class))).thenReturn(userToSave);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.LOCATION, baseUrl + "/" + id))
                .andExpect(content().json(objectMapper.writeValueAsString(new Dto<>(userToSave))));
    }

    @Test
    void save_shouldReturnBadRequest_invalidUser() throws Exception {
        testSave_shouldReturnBadRequest_InvalidUser(post(baseUrl));
    }

    @Test
    void testSave_shouldReturnBadRequest_noBody() throws Exception {
        testSave_shouldReturnBadRequest_noBody(post(baseUrl));
    }

    @Test
    void update_shouldReturnDtoUser() throws Exception {
        User userToSave = getDefaultUser();
        UUID id = UUID.randomUUID();
        userToSave.setId(id);
        when(userService.save(any(User.class))).thenReturn(userToSave);

        mockMvc.perform(put(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new Dto<>(userToSave))));
    }

    private void testSave_shouldReturnBadRequest_InvalidUser(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        User invalidUserToSave = getDefaultUser();
        invalidUserToSave.setBirthDate(LocalDate.now().plusDays(1));
        when(userService.save(invalidUserToSave)).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserToSave)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnBadRequest_invalidUser() throws Exception {
        testSave_shouldReturnBadRequest_InvalidUser(put(baseUrl));
    }

    private void testSave_shouldReturnBadRequest_noBody(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnBadRequest_noBody() throws Exception {
        testSave_shouldReturnBadRequest_noBody(put(baseUrl));
    }

    @Test
    void partialUpdate_shouldSuccess() throws Exception {
        User foundedUser = getDefaultUser();
        String newEmail = "newemail@mail.com";
        Map<String, Object> fields = Map.of("email", newEmail);
        UUID id = UUID.randomUUID();
        when(userService.partialUpdate(id.toString(), fields)).thenReturn(foundedUser);
        foundedUser.setEmail(newEmail);
        mockMvc.perform(patch(baseUrl + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new Dto<>(foundedUser))));
    }

    @Test
    void partialUpdate_shouldReturnBadRequest_noBody() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch(baseUrl + "/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }

    @Test
    void delete_shouldSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(baseUrl + "/" + id))
                .andExpect(status().isAccepted());
    }

    @Test
    void delete_shouldFail_InvalidUUID() throws Exception {
        String invalidUUID = "invalidUUID";
        doThrow(new InvalidUUIDException()).when(userService).delete(invalidUUID);
        mockMvc.perform(delete(baseUrl + "/" + invalidUUID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UUID"));
    }
}