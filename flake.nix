{
  inputs = {
    nixpkgs.url = "github:cachix/devenv-nixpkgs/rolling";
    systems.url = "github:nix-systems/default";
    devenv.url = "github:cachix/devenv";
    devenv.inputs.nixpkgs.follows = "nixpkgs";
  };

  nixConfig = {
    extra-trusted-public-keys = "devenv.cachix.org-1:w1cLUi8dv3hnoSPGAuibQv+f9TZLr6cv/Hm9XgU50cw=";
    extra-substituters = "https://devenv.cachix.org";
  };

  outputs = { self, nixpkgs, devenv, systems, ... } @ inputs:
    let
      forEachSystem = nixpkgs.lib.genAttrs (import systems);
    in
    {
      packages = forEachSystem (system: {
        devenv-up = self.devShells.${system}.default.config.procfileScript;
        devenv-test = self.devShells.${system}.default.config.test;
      });

      devShells = forEachSystem
        (system:
          let
            pkgs = nixpkgs.legacyPackages.${system};
          in
          {
            default = devenv.lib.mkShell {
              inherit inputs pkgs;
              modules = [
                {
                  # https://devenv.sh/reference/options/
                  packages = with pkgs; [
                    gtk3
                    scala
                    libGL
                    xorg.libXxf86vm
                    xorg.libXtst
                    glib.out
                    alsa-lib
                    ffmpeg
                    ( sbt.override { jre = jdk17; } )
                  ];

                  enterShell = ''
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.xorg.libXxf86vm}/lib/";
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.libGL}/lib/";
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.glib.out}/lib/";
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.gtk3}/lib/";
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.xorg.libXtst}/lib/";
                    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.alsa-lib}/lib/";
                  '';
                }
              ];
            };
          });
    };
}
