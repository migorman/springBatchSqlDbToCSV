package ma.batch.dbToCsv;

import org.springframework.batch.item.ItemProcessor;

import ma.batch.dbToCsv.model.User;

public class UserIemProcessor  implements ItemProcessor<User, User>{

	@Override
	public User process(User user) throws Exception {
		return user;
	}



}
