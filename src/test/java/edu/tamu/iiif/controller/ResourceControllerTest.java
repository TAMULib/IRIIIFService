package edu.tamu.iiif.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.tamu.iiif.model.repo.RedisResourceRepo;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ResourceController.class, secure = false)
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisResourceRepo redisResourceRepo;

    @Test
    public void testGetResources() {

    }

    @Test
    public void testGetResourceUrl() {

    }

    @Test
    public void testRedirectToResource() {

    }

    @Test
    public void testGetResourceId() {

    }

    @Test
    public void testPutResource() {

    }

    @Test
    public void testRemoveResource() {

    }

}
