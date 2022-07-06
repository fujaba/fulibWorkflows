package org.fulib.workflows.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin()
public class ExampleController
{
	@GetMapping(path = "/api/v1/examples", produces = "application/json")
	@ResponseBody
	public byte[] index() throws Exception
	{
		return getClass().getResourceAsStream("/examples/index.json").readAllBytes();
	}

	@GetMapping(path = "/api/v1/examples/{path}", produces = "text/yaml")
	@ResponseBody
	public byte[] getExample(@PathVariable("path") String path) throws Exception
	{
		return getClass().getResourceAsStream("/examples/" + path).readAllBytes();
	}
}
