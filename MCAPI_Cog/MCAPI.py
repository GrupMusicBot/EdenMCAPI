import json
import discord
import time

import requests
from discord.ext import commands
import pymongo
from pymongo import MongoClient
from datetime import datetime
import socket

mongo_url = "MongoURL"
cluster = MongoClient(mongo_url)
db = cluster["MC"]
DBCollection = db["API"]

class MCAPI(commands.Cog):
    """MC API Commands
    Github : https://github.com/GrupMusicBot/EdenMCAPI"""

    @commands.group(name="api", invoke_without_command=True)
    async def api(self, ctx):
        helpEmbed = discord.Embed(title="MC API Help Menu", color=discord.Color.red())
        helpEmbed.add_field(name="__Subcommands : __", value=f"**link** - Link MC & Discord Account\n**profile** - View an MC Profile that has joined the server", inline=False)
        await ctx.send(embed=helpEmbed)

    @api.command(name="profile")
    async def profile(self, ctx , username):
        # If the User has ever joined the server, the DB will return a value
        # If the user has never joined the server, the DB will not return anything other than their name
        
        # Need to add override command for staff members of the server

        APINotEnabled = discord.Embed(title="User does not have their API Enabled!", color=discord.Color.red())
        # Pre define this

        results = DBCollection.find({"Username": username})
        try:
            e = discord.Embed(title=f"{username}'s Profile", color=discord.Color.blurple())
            for result in results:
                Enabled = (result["APIEnabled"])
                if not Enabled:
                    await ctx.send(embed=APINotEnabled)
                    return

                PlayerKills = (result["PlayerKills"])
                MobKills = (result["MobKills"])
                TotalDeaths = (result["Deaths"])
                Playtime = (result["Playtime"])
                Distance = (result["DistanceTravelled"])
                
                if Playtime > 60:
                    Playtime = Playtime / 60
                    if Playtime > 24:
                        Playtime = Playtime / 24
                        PlayTimeString = f"{round(Playtime, 1)} Days"
                    else:
                        PlayTimeString = f"{round(Playtime, 1)} Hours"
                else:
                    PlayTimeString = f"{round(Playtime, 1)} Minutes"

                e.add_field(name="Mob Kills", value=MobKills, inline=True)
                e.add_field(name="Player Kills", value=PlayerKills, inline=True)
                e.add_field(name="Total Kills", value=MobKills+PlayerKills, inline=True)
                e.add_field(name="Total Deaths", value=TotalDeaths, inline=False)
                e.add_field(name="Distance Travelled", value=f"{Distance/100} Blocks", inline=False)
                e.add_field(name="Playtime", value=PlayTimeString, inline=False)
                
            await ctx.send(embed=e)
        except:
            er = discord.Embed(title="User has never joined the server!")
            await ctx.send(embed=er)


