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

    @api.command(name="link")
    async def link(self, ctx , code):
        if len(code) > 6:
            # API Link Codes are never more than 6 characters
            await ctx.send("That doesn't look like a valid code!")
            return

        result = DBCollection.find({"AuthCode": code})
        try:
            for results in result:
                Username = (results["Username"])
                UUID = (results["_id"])

            UpdateCode = DBCollection.update_one({"_id": UUID}, {
                "$set": {"AuthCode": "Linked", "DiscordName": ctx.author.name, "DiscordID": ctx.author.id}})
            AccountLinked = discord.Embed(title="Account Linked!", color=discord.Color.green())
            AccountLinked.add_field(name="Username & UUID", value=f"{Username} | ({UUID})")
            await ctx.send(embed=AccountLinked)
        except Exception as e:
            errorEmbed = discord.Embed(title="An Error has Occured", color=discord.Color.red())
            errorEmbed.add_field(name="Error Message", value=f"```{type(e).__name__}```")
            await ctx.send(embed=errorEmbed)

    @api.command(name="profile")
    async def profile(self, ctx , username):
        # If the User has ever joined the server, the DB will return a value
        # If the user has never joined the server, the DB will not return anything other than their name

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

                TotalKills = (result["TotalKills"])
                TotalDeaths = (result["TotalDeaths"])
                Playtime = (result["Playtime"])
                DiscordName = (result["DiscordName"])
                DiscordID = (result["DiscordID"])

                e.add_field(name="Total Kills (Mob + Player)", value=TotalKills, inline=False)
                e.add_field(name="Total Deaths", value=TotalDeaths, inline=False)
                e.add_field(name="Playtime (in minutes)", value=Playtime, inline=False)

                if DiscordName == "None":
                    e.add_field(name="Discord User", value="N/A", inline=False)
                else:
                    e.add_field(name="Discord User", value=f"<@!{DiscordID}>", inline=False)
            await ctx.send(embed=e)
        except:
            er = discord.Embed(title="User has never joined the server!")
            await ctx.send(embed=er)


