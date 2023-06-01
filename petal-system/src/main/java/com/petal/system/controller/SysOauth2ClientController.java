package com.petal.system.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petal.oauth2.common.base.entity.SysOauth2Client;
import com.petal.oauth2.common.base.utils.ResponseResult;
import com.petal.oauth2.common.security.annotation.PermitAll;
import com.petal.oauth2.resource7000.service.SysOauth2ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import java.util.List;

@Api("oauth2-client控制器")
@RestController
@RequestMapping("/client")
public class SysOauth2ClientController {

	private SysOauth2ClientService sysOauth2ClientService;

	@Autowired
	public void setSysOauth2ClientService(SysOauth2ClientService sysOauth2ClientService) {
		this.sysOauth2ClientService = sysOauth2ClientService;
	}

	/**
	 * 通过客户端id查询Oauth2客户端信息
	 *
	 * @param clientId 客户机id
	 * @return {@link ResponseResult}
	 */
	@PermitAll
	@GetMapping("/getClientById/{clientId}")
	@ApiOperation("通过客户端id查询Oauth2客户端信息")
	public ResponseResult getClientById(@PathVariable String clientId) {
		SysOauth2Client sysOauth2Client = sysOauth2ClientService.getOne(
				Wrappers.<SysOauth2Client>lambdaQuery().eq(SysOauth2Client::getClientId, clientId), false);
		String json = JSON.toJSONString(sysOauth2Client);
		return ResponseResult.ok(json);
	}

	/**
	 * 通过客户端id查询Oauth2客户端信息
	 *
	 * @param clientId 客户端id
	 * @return sysOauthClient
	 */
	@GetMapping("/{clientId}")
	@ApiOperation("通过客户端id查询Oauth2客户端信息")
	public ResponseResult<List<SysOauth2Client>> getByClientId(@PathVariable String clientId) {
		return ResponseResult.ok(sysOauth2ClientService
			.list(Wrappers.<SysOauth2Client>lambdaQuery().eq(SysOauth2Client::getClientId, clientId)));
	}

	/**
	 * 分页查询Oauth2客户端信息
	 *
	 * @param page 分页对象
	 * @param sysOauth2Client 系统终端
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询Oauth2客户端信息")
	public ResponseResult<IPage<SysOauth2Client>> getOauthClientPage(Page page,
																	 SysOauth2Client sysOauth2Client) {
		return ResponseResult.ok(sysOauth2ClientService.page(page, Wrappers.query(sysOauth2Client)));
	}

	/**
	 * 添加Oauth2客户端信息
	 *
	 * @param sysOauth2Client 实体
	 * @return success/false
	 */
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_client_add')")
	@ApiOperation("添加Oauth2客户端信息")
	public ResponseResult<Boolean> add(@Valid @RequestBody SysOauth2Client sysOauth2Client) {

		return ResponseResult.ok(sysOauth2ClientService.save(sysOauth2Client));
	}

	/**
	 * 删除指定Oauth2客户端id的数据
	 *
	 * @param id 客户端id
	 * @return success/false
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_client_del')")
	@ApiOperation("删除指定Oauth2客户端id的数据")
	public ResponseResult<Boolean> removeById(@PathVariable String id) {
		return ResponseResult.ok(sysOauth2ClientService.removeClientById(id));
	}

	/**
	 * 修改Oauth2客户端信息
	 *
	 * @param sysOauth2Client 实体
	 * @return success/false
	 */
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_client_edit')")
	@ApiOperation("修改Oauth2客户端信息")
	public ResponseResult<Boolean> update(@Valid @RequestBody SysOauth2Client sysOauth2Client) {
		return ResponseResult.ok(sysOauth2ClientService.updateClientById(sysOauth2Client));
	}

	/**
	 * 清除客户端缓存
	 *
	 * @return {@link ResponseResult}
	 */
	@DeleteMapping("/cache")
	@PreAuthorize("@pms.hasPermission('sys_client_del')")
	@ApiOperation("清除客户端缓存")
	public ResponseResult clearClientCache() {
		sysOauth2ClientService.clearClientCache();
		return ResponseResult.ok();
	}


}
