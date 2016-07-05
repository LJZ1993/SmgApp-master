package xunao.fq_mobile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class api {
	// 测试服务器
	//private static String apiHost = "http://42.120.20.222:85/api/";

	// private static String apiHost = "http://222.68.17.101/api/";

	// 正式服务器
	private static String apiHost = "http://222.68.17.116/";

	/**
	 * 登录接口
	 * 
	 * @param String
	 *            username 用户工号
	 * @param String
	 *            password 用户密码
	 * @return String
	 */
	public static String user_login(String username, String password) {
		return apiHost + "user/login.php?username=" + username + "&password="
				+ password;
	}

	/**
	 * 用户密码修改
	 * 
	 * @param int type 修改密码类型：1、老密码，2、验证码
	 * @param String
	 *            username 用户工号
	 * @param String
	 *            new_password 新密码
	 * @param String
	 *            yz 对应类型：1、老密码，2、验证码
	 * @return String
	 */
	public static String user_change(int type, String username,
			String new_password, String yz) {
		String url;
		if (type == 1) {
			url = apiHost + "user/change.php?type=" + type + "&username="
					+ username + "&new_password=" + new_password
					+ "&old_password=" + yz;
		} else {
			url = apiHost + "user/change.php?type=" + type + "&username="
					+ username + "&new_password=" + new_password + "&yz_code="
					+ yz;
		}
		return url;
	}

	/**
	 * 用户找回密码
	 * 
	 * @param String
	 *            username 用户工号
	 * @param String
	 *            person_id 用户身份证后六位
	 * @param String
	 *            phone 用户手机号 return String
	 */
	public static String user_find(String username, String person_id,
			String phone) {
		return apiHost + "user/find.php?username=" + username + "&person_id="
				+ person_id + "&phone=" + phone;
	}

	/**
	 * 用户状态
	 * 
	 * @param String
	 *            username 用户工号 return String
	 */
	public static String user_state(String username) {
		return apiHost + "user/state.php?username=" + username;
	}

	/**
	 * 新闻列表
	 * 
	 * @param　int page 页码
	 * @return String
	 */
	public static String news_list(int page, String categoryid) {
		return apiHost + "news/list.php?category_id=" + categoryid + "&page="
				+ page;
	}

	/**
	 * 新闻详细
	 * 
	 * @param String
	 *            id 新闻id
	 * @return String
	 */
	public static String news_detail(String id, String username) {
		// return apiHost + "news/detail.php?id=" + id;
		return apiHost + "news/detail2.php?id=" + id + "&username=" + username;
	}

	/**
	 * 团购详细
	 * 
	 * @param String
	 *            id 新闻id
	 * @return String
	 */
	public static String group_detail(String id, String username) {
		// return apiHost + "news/detail.php?id=" + id;
		return apiHost + "group/detail.php?id=" + id + "&username=" + username;
	}

	/**
	 * 提交团购
	 * 
	 * @param id
	 *            新闻id
	 * @param loginname
	 *            工号
	 * @param spname
	 *            商品名称
	 * @param num
	 *            数量
	 * @param username
	 *            用户姓名
	 * @param phone
	 *            联系电话
	 * @param address
	 *            下拉框地址
	 * @param custom_address
	 *            用户自定义地址
	 * @param remark
	 *            备注
	 * @return string
	 */
	public static String group_submit() {
		return apiHost + "group/buy_info.php";
	}

	/**
	 * 专题列表
	 * 
	 * @param String
	 *            id 新闻id
	 * @return String
	 */
	public static String subject_list(String id) {
		return apiHost + "subject/list.php?id=" + id;
	}

	/**
	 * 评论数
	 * 
	 * @param String
	 *            id 评论对应的新闻or投票or专题的id
	 * @param int type 类型1、新闻，2、投票，3、调查
	 * @return String
	 */
	public static String comment_count(String id, int type) {
		return apiHost + "comment/count.php?id=" + id + "&type=" + type;
	}

	/**
	 * 评论列表
	 * 
	 * @param String
	 *            id 评论对应的新闻or投票or专题的id
	 * @param int page 当前评论列表页码
	 * @param int type 类型1、新闻，2、投票，3、调查
	 * @return String
	 */
	public static String comment_list(String username, String id, int page,
			int type) {
		return apiHost + "comment/list.php?id=" + id + "&page=" + page
				+ "&type=" + type + "&username=" + username;
	}

	/**
	 * 发表评论
	 * 
	 * @param String
	 *            id 评论对应的新闻or投票or专题的id
	 * @param int type 类型1、新闻，2、投票，3、调查
	 * @param content
	 *            评论内容
	 * @param nickname
	 *            昵称
	 * @return String
	 */
	public static String comment_comment(String id, int type, String nickname,
			String username, String resource_type, String resource_id,
			String photo_ids, String parent_id) {
		return apiHost + "comment/comment.php?id=" + id + "&type=" + type
				+ "&nickname=" + nickname + "&username=" + username
				+ "&resource_type=" + resource_type + "&resource_id="
				+ resource_id + "&photo_ids=" + photo_ids + "&parent_id="
				+ parent_id;
	}

	/**
	 * 获取投票内容页
	 * 
	 * @param String
	 *            id 新闻id
	 * @return String
	 */
	public static String vote_detail(String id) {
		// return apiHost + "vote/detail.php?id=" + id;
		return apiHost + "vote/detail2.php?id=" + id;
	}

	/**
	 * 投票执行
	 * 
	 * @param String
	 *            id 投票id
	 * @param String
	 *            username 用户工号
	 * @return String
	 */
	public static String vote_vote(String id, String username) {
		return apiHost + "vote/vote.php?id=" + id + "&username=" + username;
	}

	/**
	 * 投票人数
	 * 
	 * @param String
	 *            id 投票id
	 * @return String
	 */
	public static String vote_num(String id) {
		return apiHost + "vote/num.php?id=" + id;
	}

	public static String find(String username, String person_id, String phone,
			int type) {
		return apiHost + "user/find.php?username=" + username + "&person_id="
				+ person_id + "&phone=" + phone + "&type=" + type;
	}

	public static String register(String username, String yz, String phone,
			String password) {
		return apiHost + "user/register.php?username=" + username + "&yz=" + yz
				+ "&phone=" + phone + "&password=" + password;
	}

	/**
	 * 投票结果
	 * 
	 * @param String
	 *            id 投票id
	 * @return String
	 */
	public static String vote_result(String id) {
		// return apiHost + "vote/result.php?id=" + id;
		return apiHost + "vote/result2.php?id=" + id;
	}

	/**
	 * 获取调查内容页
	 * 
	 * @param String
	 *            id 新闻id
	 * @return String
	 */
	public static String research_detail(String id) {
		// return apiHost + "research/detail.php?id=" + id;
		return apiHost + "research/detail2.php?id=" + id;
	}

	/**
	 * 调查提交
	 * 
	 * @param String
	 *            id 调查id
	 * @param String
	 *            item_ids 被投票项id
	 * @param String
	 *            username 用户工号
	 * @return String
	 */
	public static String research_research(String id, String username) {
		return apiHost + "research/research.php?id=" + id + "&username="
				+ username;
	}

	/**
	 * 调查人数
	 * 
	 * @param String
	 *            id 调查id
	 * @return String
	 */
	public static String research_num(String id) {
		return apiHost + "research/num.php?id=" + id;
	}

	/**
	 * 调查结果
	 * 
	 * @param String
	 *            id 调查id
	 * @return String
	 */
	public static String research_result(String id) {
		// return apiHost + "research/result.php?id=" + id;
		return apiHost + "research/result2.php?id=" + id;
	}

	/**
	 * 更新版本
	 */
	public static String update_update(String string) {
		// return "http://42.120.20.222:85/api/update/update.php?version=" +
		// string;
		return "http://180.166.160.17:10000/update/update.php?version=" + string;
	}

	/**
	 * 收藏新闻
	 */
	public static String favorite_news(String username, String resource_id) {
		// return "http://42.120.20.222:85/api/update/update.php?version=" +
		// string;
		return apiHost + "favorite/favorite.php?username=" + username
				+ "&resource_id=" + resource_id;
	}

	/**
	 * 取消收藏新闻
	 */
	public static String cancel_favorite_news(String username,
			String resource_id) {
		// return "http://42.120.20.222:85/api/update/update.php?version=" +
		// string;
		return apiHost + "favorite/cancleFavorite.php?username=" + username
				+ "&resource_id=" + resource_id;
	}

	/**
	 * 我的收藏新闻
	 */
	public static String my_favorite(String username, int page) {
		return apiHost + "favorite/myFavorite.php?username=" + username
				+ "&page=" + page;
	}

	/**
	 * 我的评论
	 */
	public static String my_comment(String username, int page) {
		return apiHost + "comment/myComment.php?username=" + username
				+ "&page=" + page;
	}

	/**
	 * 评论点赞
	 */
	public static String give_flower(String username, int comment_id) {
		return apiHost + "comment/giveFlower.php?username=" + username
				+ "&comment_id=" + comment_id;
	}

	/**
	 * 搜索新闻列表
	 * 
	 * @param　int page 页码
	 * @param　String searchval
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String search_list(int page, String searchval)
			throws UnsupportedEncodingException {
		return apiHost + "search/list.php?page=" + page + "&search="
				+ URLEncoder.encode(searchval, "utf-8");
	}

	public static String search_shortphone(String searchval)
			throws UnsupportedEncodingException {
		return apiHost + "phone/search_shortphone.php?name="
				+ URLEncoder.encode(searchval, "utf-8");
	}

	public static String search_shortextension(String searchval)
			throws UnsupportedEncodingException {
		return apiHost + "phone/search_extension.php?name="
				+ URLEncoder.encode(searchval, "utf-8");
	}

	/**
	 * 获取分类
	 */
	public static String get_category_list() {
		return apiHost + "category/list.php";
	}

	/**
	 * 上传图片
	 * */
	public static String comment_upload_image() {
		return apiHost + "comment/uploadphoto.php";
	}
}
