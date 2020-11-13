package to.thepiratebay.stc;

import java.util.Arrays;
import java.util.List;

import discord4j.core.object.util.Snowflake;

public class StaticID {
	
	public static final Snowflake GUILD = Snowflake.of(610259379945472022L);
	
	public static final Snowflake PRIVATE_CAT = Snowflake.of(610270249568436225L);
	
	public static final Snowflake INFO = Snowflake.of(610259380696514562L);
	public static final Snowflake ANNONCE = Snowflake.of(611696490640834560L);
	public static final Snowflake TUTORIEL = Snowflake.of(611696597226618883L);
	public static final Snowflake ADMIN = Snowflake.of(610269857845477396L);
	public static final Snowflake LIVE = Snowflake.of(610466030522859565L);
	public static final Snowflake POLICE = Snowflake.of(610466109069459501L);
	public static final Snowflake PUBLIC = Snowflake.of(612016491164073984L);
	
	public static final Snowflake RESP_RANK = Snowflake.of(610269611140710436L);
	public static final Snowflake STAFF_RANK = Snowflake.of(610259786398695424L);
	public static final Snowflake POLICE_RANK = Snowflake.of(610259786398695424L);
	
	public static final List<Snowflake> POLICES = Arrays.asList(
																Snowflake.of(496719006544166933L),
																Snowflake.of(347149283851763713L),
																Snowflake.of(573854580559839233L)
																);

	public static final List<Snowflake> STAFFS = Arrays.asList(
																Snowflake.of(379021447420575744L),
																Snowflake.of(100227684906340352L),
																Snowflake.of(361560815578513408L)
																);
	
}
