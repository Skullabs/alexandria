package alexandria.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PackageDescription {

	@NonNull String maintainer;
	@NonNull String name;
	@NonNull String version;
}
