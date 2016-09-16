function fatal {
  comment_on_pull "Rebase failed ($BUILD_URL): $1"
  exit 1
}

function comment_on_pull
{
    if [ "$COMMENT_ON_PULL" = "" ]; then return; fi

    PULL_NUMBER=$(echo $GIT_BRANCH | awk -F 'pull' '{ print $2 }' | awk -F '/' '{ print $2 }')
    if [ "$PULL_NUMBER" != "" ]
    then
        JSON="{ \"body\": \"$1\" }"
        curl -d "$JSON" -ujbosstm-bot:$BOT_PASSWORD https://api.github.com/repos/$GIT_ACCOUNT/$GIT_REPO/issues/$PULL_NUMBER/comments
    else
        echo "Not a pull request, so not commenting"
    fi
}

function rebase {
  echo "Rebasing"
  cd $WORKSPACE

  # Clean up the local repo
  git rebase --abort
  rm -rf .git/rebase-apply
  git clean -f -d -x

  # Work out the branch point
  git branch -D master
  git branch master origin/master
  # Update the pull to head  
  git pull --rebase --ff-only origin master

  if [ $? -ne 0 ]; then
    fatal "Rebase on master failed. Please rebase it manually"
  fi
}

rebase "$@"
exit 0