package com.originmobi.boteco.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.originmobi.boteco.enumerados.ComandaStatus;
import com.originmobi.boteco.model.Cliente;
import com.originmobi.boteco.model.Comanda;
import com.originmobi.boteco.model.Mesa;
import com.originmobi.boteco.model.Produto;
import com.originmobi.boteco.service.ClienteService;
import com.originmobi.boteco.service.ComandaService;
import com.originmobi.boteco.service.MesaService;
import com.originmobi.boteco.service.ProdutoService;

@Controller
@RequestMapping("/comanda")
public class ComandaController {

	private static final String VIEW_FORM = "formComanda";
	private static final String VIEW_LIST = "listaComanda";
	private static final String VIEW_PROD_COMANDA = "comandaProdutos";

	@Autowired
	private ComandaService comandaService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private MesaService mesaService;

	@Autowired
	private ProdutoService produtoService;

	@GetMapping("/formulario")
	public ModelAndView formulario() {
		ModelAndView mv = new ModelAndView(VIEW_FORM);
		mv.addObject(new Comanda());

		return mv;
	}

	@PostMapping
	public String novo(Comanda comanda) {
		comandaService.cadastrar(comanda);

		return "redirect:/comanda/formulario";
	}

	@RequestMapping(value = "/novoProduto", method = RequestMethod.PUT)
	public @ResponseBody String insereProduto(@RequestParam Map<String, String> requestParam) {
		Long codigoComanda = Long.decode(requestParam.get("codigoComanda"));
		Long codigoProduto = Long.decode(requestParam.get("codigoProduto"));

		return comandaService.adicionaProdutoComanda(codigoComanda, codigoProduto);

	}

	@GetMapping
	public ModelAndView lista() {
		List<Comanda> todasComandas = new ArrayList<Comanda>();
		todasComandas = comandaService.lista();

		ModelAndView mv = new ModelAndView(VIEW_LIST);
		mv.addObject("comandas", todasComandas);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Comanda comanda) {
		ModelAndView mv = new ModelAndView(VIEW_FORM);
		mv.addObject("comanda", comanda);
		return mv;
	}

	@GetMapping("/excluir/{codigo}")
	public String remover(@PathVariable("codigo") Long codigo) {
		comandaService.excluir(codigo);
		return "redirect:/comanda";
	}

	@GetMapping("/produtosComanda/{codigo}")
	public ModelAndView produtosDaComanda(@PathVariable("codigo") Comanda comanda) {
		ModelAndView mv = new ModelAndView(VIEW_PROD_COMANDA);
		mv.addObject("comanda", comanda);
		return mv;
	}

	@RequestMapping(value = "/creditar", method = RequestMethod.PUT)
	public @ResponseBody String creditarComanda(@RequestParam Map<String, String> requestParam) {
		Long codigo_comanda = Long.decode(requestParam.get("codigoComanda"));
		Double credito = Double.valueOf(requestParam.get("credito"));

		return comandaService.creditar(codigo_comanda, credito);
	}

	@RequestMapping(value = "/cancelar/{codigo}", method = RequestMethod.POST)
	public @ResponseBody String cancelarComanda(@PathVariable("codigo") Long codigo) {
		comandaService.cancelar(codigo);
		
		return "ok";
	}

	@ModelAttribute("statusComanda")
	public List<ComandaStatus> statusComanda() {
		return Arrays.asList(ComandaStatus.values());
	}

	@ModelAttribute("todosClientes")
	public List<Cliente> todosClientes() {
		List<Cliente> todosClientes = new ArrayList<Cliente>();
		todosClientes = clienteService.listar();
		return todosClientes;
	}

	@ModelAttribute("todasMesas")
	public List<Mesa> todasMesas() {
		return this.mesaService.lista();
	}

	@ModelAttribute("todosProdutos")
	public List<Produto> todosProdutos() {
		return this.produtoService.listar();
	}

}
