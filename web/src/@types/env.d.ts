interface ImportMetaEnv {
  readonly NG_APP_SUPABASE_URL: string;
  readonly NG_APP_SUPABASE_ANON_KEY: string;
  [key: string]: string | undefined;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare var process: {
  env: {
    NG_APP_SUPABASE_URL: string;
    NG_APP_SUPABASE_ANON_KEY: string;
    [key: string]: string | undefined;
  };
};
